package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import mx.neology.acceso.application.modelo.ResultadoIniciarMes;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IniciarMesTest {

  @Mock PeriodoRepositorio periodos;

  @Test
  void cierraElActivoConLaHoraDelRelojYAbreUnoNuevo() {
    Periodo activo = Fixtures.periodoActivo();
    when(periodos.activo()).thenReturn(activo);
    when(periodos.cerrar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(periodos.abrir(any())).thenAnswer(inv -> inv.getArgument(0));
    IniciarMes casoDeUso = new IniciarMes(periodos, new TransaccionDirecta(), Fixtures.RELOJ);

    ResultadoIniciarMes resultado = casoDeUso.ejecutar();

    assertThat(resultado.periodoAnterior().id()).isEqualTo(activo.id());
    assertThat(resultado.periodoAnterior().fin()).isEqualTo(Fixtures.AHORA);
    assertThat(resultado.periodoAnterior().esActivo()).isFalse();
    assertThat(resultado.periodoActual().inicio()).isEqualTo(Fixtures.AHORA);
    assertThat(resultado.periodoActual().fin()).isNull();
    assertThat(resultado.periodoActual().esActivo()).isTrue();
    assertThat(resultado.periodoActual().id()).isNotEqualTo(activo.id());
    InOrder orden = Mockito.inOrder(periodos);
    ArgumentCaptor<Periodo> cerrado = ArgumentCaptor.forClass(Periodo.class);
    orden.verify(periodos).cerrar(cerrado.capture());
    orden.verify(periodos).abrir(any());
    assertThat(cerrado.getValue().activo()).isNull();
  }
}
