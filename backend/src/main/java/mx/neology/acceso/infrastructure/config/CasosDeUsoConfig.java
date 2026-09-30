package mx.neology.acceso.infrastructure.config;

import java.time.Clock;
import mx.neology.acceso.application.ConsultarVehiculos;
import mx.neology.acceso.application.GenerarInformePagos;
import mx.neology.acceso.application.IniciarMes;
import mx.neology.acceso.application.RegistrarEntrada;
import mx.neology.acceso.application.RegistrarSalida;
import mx.neology.acceso.application.RegistrarVehiculo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Los casos de uso no llevan anotaciones de Spring: se registran aquí como beans. */
@Configuration
public class CasosDeUsoConfig {

  @Bean
  RegistrarVehiculo registrarVehiculo(
      VehiculoRepositorio vehiculos, Transaccion transaccion, Clock clock) {
    return new RegistrarVehiculo(vehiculos, transaccion, clock);
  }

  @Bean
  RegistrarEntrada registrarEntrada(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarEntrada(vehiculos, estancias, calculadora, transaccion, clock);
  }

  @Bean
  RegistrarSalida registrarSalida(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarSalida(
        vehiculos, estancias, periodos, acumulados, calculadora, transaccion, clock);
  }

  @Bean
  IniciarMes iniciarMes(PeriodoRepositorio periodos, Transaccion transaccion, Clock clock) {
    return new IniciarMes(periodos, transaccion, clock);
  }

  @Bean
  GenerarInformePagos generarInformePagos(
      VehiculoRepositorio vehiculos,
      PeriodoRepositorio periodos,
      CalculadoraCobro calculadora,
      Transaccion transaccion) {
    return new GenerarInformePagos(vehiculos, periodos, calculadora, transaccion);
  }

  @Bean
  ConsultarVehiculos consultarVehiculos(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new ConsultarVehiculos(
        vehiculos, estancias, periodos, acumulados, calculadora, transaccion, clock);
  }
}
