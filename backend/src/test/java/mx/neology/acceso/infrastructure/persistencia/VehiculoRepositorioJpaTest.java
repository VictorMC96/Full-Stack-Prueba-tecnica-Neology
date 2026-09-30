package mx.neology.acceso.infrastructure.persistencia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({VehiculoRepositorioJpa.class, TipoVehiculoRepositorioJpa.class})
class VehiculoRepositorioJpaTest {

  private static final UUID PERIODO = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final CodigoTipo RESIDENTE = new CodigoTipo("RESIDENTE");
  private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-09-15T12:00:00-06:00");

  @Autowired VehiculoRepositorioJpa repositorio;
  @Autowired TipoVehiculoRepositorioJpa tipos;
  @Autowired VehiculoJpaRepository jpa;

  @Test
  void encuentraUnVehiculoDeLaPrecarga() {
    assertThat(repositorio.buscarPorPlaca(Placa.de("OFI001")))
        .isPresent()
        .get()
        .satisfies(v -> assertThat(v.tipo().valor()).isEqualTo("OFICIAL"));
    assertThat(repositorio.buscarPorPlacaParaActualizar(Placa.de("OFI002")))
        .isPresent()
        .get()
        .satisfies(v -> assertThat(v.dentro()).isTrue());
    assertThat(repositorio.contar()).isEqualTo(6);
    assertThat(repositorio.contarPorTipo(RESIDENTE)).isEqualTo(3);
    assertThat(repositorio.conteoPorTipo())
        .containsExactlyInAnyOrderEntriesOf(
            Map.of(
                new CodigoTipo("OFICIAL"), 2L, RESIDENTE, 3L, new CodigoTipo("NO_RESIDENTE"), 1L));
  }

  @Test
  void placaDuplicadaViolaLaRestriccionUnica() {
    Vehiculo duplicado = Vehiculo.nuevo(Placa.de("OFI001"), RESIDENTE, AHORA);
    assertThatThrownBy(
            () -> {
              repositorio.guardar(duplicado);
              jpa.flush();
            })
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void unTipoInexistenteViolaLaClaveForanea() {
    Vehiculo sinTipo = Vehiculo.nuevo(Placa.de("XXX001"), new CodigoTipo("FANTASMA"), AHORA);
    assertThatThrownBy(
            () -> {
              repositorio.guardar(sinTipo);
              jpa.flush();
            })
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void listaConFiltroOrdenYPaginacionEnLaConsulta() {
    Pagina<Vehiculo> pagina = repositorio.listar("RES", new ConsultaPagina(0, 2, "placa", false));
    assertThat(pagina.totalElementos()).isEqualTo(3);
    assertThat(pagina.totalPaginas()).isEqualTo(2);
    assertThat(pagina.contenido())
        .extracting(v -> v.placa().valor())
        .containsExactly("RES003", "RES002");
  }

  @Test
  void acumuladosPorTipoTraeCerosParaResidentesSinFila() {
    repositorio.guardar(Vehiculo.nuevo(Placa.de("RES010"), RESIDENTE, AHORA));
    List<FilaInforme> filas = repositorio.acumuladosPorTipo(Set.of(RESIDENTE), PERIODO);
    assertThat(filas)
        .extracting(FilaInforme::placa)
        .containsExactly("RES001", "RES002", "RES003", "RES010");
    assertThat(filas.get(0).minutos()).isEqualTo(210);
    assertThat(filas.get(0).importe()).isEqualByComparingTo("10.50");
    assertThat(filas.get(3).minutos()).isZero();
    assertThat(filas.get(3).importe()).isEqualByComparingTo("0");
    assertThat(repositorio.acumuladosPorTipo(Set.of(), PERIODO)).isEmpty();
  }

  @Test
  void elCatalogoDeTiposSeLeeYSeEscribe() {
    assertThat(tipos.listar())
        .extracting(t -> t.codigo().valor())
        .containsExactly("NO_RESIDENTE", "OFICIAL", "RESIDENTE");
    assertThat(tipos.provisional().codigo().valor()).isEqualTo("NO_RESIDENTE");
    assertThat(tipos.buscar(new CodigoTipo("RESIDENTE")))
        .isPresent()
        .get()
        .satisfies(
            t -> {
              assertThat(t.modeloCobro()).isEqualTo(ModeloCobro.POR_MINUTO_ACUMULADO);
              assertThat(t.tarifaPorMinuto()).isEqualByComparingTo("0.05");
              assertThat(t.integrado()).isTrue();
            });
    TipoVehiculo visitante =
        tipos.guardar(
            TipoVehiculo.nuevo(
                new CodigoTipo("VISITANTE"),
                "Visitante",
                ModeloCobro.POR_MINUTO_AL_SALIR,
                new BigDecimal("0.30"),
                AHORA));
    assertThat(tipos.listar()).hasSize(4);
    assertThat(tipos.buscar(visitante.codigo())).isPresent();
    tipos.eliminar(visitante.codigo());
    assertThat(tipos.buscar(visitante.codigo())).isEmpty();
  }
}
