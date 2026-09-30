package mx.neology.acceso.infrastructure.persistencia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.Pagina;
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
@Import(VehiculoRepositorioJpa.class)
class VehiculoRepositorioJpaTest {

  private static final UUID PERIODO = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Autowired VehiculoRepositorioJpa repositorio;
  @Autowired VehiculoJpaRepository jpa;

  @Test
  void encuentraUnVehiculoDeLaPrecarga() {
    assertThat(repositorio.buscarPorPlaca(Placa.de("OFI001")))
        .isPresent()
        .get()
        .satisfies(v -> assertThat(v.tipo()).isEqualTo(TipoVehiculo.OFICIAL));
    assertThat(repositorio.buscarPorPlacaParaActualizar(Placa.de("OFI002")))
        .isPresent()
        .get()
        .satisfies(v -> assertThat(v.dentro()).isTrue());
    assertThat(repositorio.contar()).isEqualTo(6);
  }

  @Test
  void placaDuplicadaViolaLaRestriccionUnica() {
    OffsetDateTime ahora = OffsetDateTime.parse("2026-09-15T12:00:00-06:00");
    Vehiculo duplicado = Vehiculo.nuevo(Placa.de("OFI001"), TipoVehiculo.RESIDENTE, ahora);
    assertThatThrownBy(
            () -> {
              repositorio.guardar(duplicado);
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
  void ordenaDentroPrimeroYDesempataPorPlaca() {
    Pagina<Vehiculo> pagina = repositorio.listar("", new ConsultaPagina(0, 10, "dentro", false));
    assertThat(pagina.contenido())
        .extracting(v -> v.placa().valor())
        .containsExactly("OFI002", "NOR001", "OFI001", "RES001", "RES002", "RES003");
  }

  @Test
  void acumuladosPorTipoTraeCerosParaResidentesSinFila() {
    OffsetDateTime ahora = OffsetDateTime.parse("2026-09-15T12:00:00-06:00");
    repositorio.guardar(Vehiculo.nuevo(Placa.de("RES010"), TipoVehiculo.RESIDENTE, ahora));
    List<FilaInforme> filas =
        repositorio.acumuladosPorTipo(Set.of(TipoVehiculo.RESIDENTE), PERIODO);
    assertThat(filas)
        .extracting(FilaInforme::placa)
        .containsExactly("RES001", "RES002", "RES003", "RES010");
    assertThat(filas.get(0).minutos()).isEqualTo(210);
    assertThat(filas.get(0).importe()).isEqualByComparingTo("10.50");
    assertThat(filas.get(3).minutos()).isZero();
    assertThat(filas.get(3).importe()).isEqualByComparingTo("0");
    assertThat(repositorio.acumuladosPorTipo(Set.of(), PERIODO)).isEmpty();
  }
}
