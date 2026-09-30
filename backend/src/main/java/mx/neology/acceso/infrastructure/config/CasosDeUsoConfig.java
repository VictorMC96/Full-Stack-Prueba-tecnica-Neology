package mx.neology.acceso.infrastructure.config;

import java.time.Clock;
import mx.neology.acceso.application.ActualizarTipoVehiculo;
import mx.neology.acceso.application.ConsultarTiposVehiculo;
import mx.neology.acceso.application.ConsultarVehiculos;
import mx.neology.acceso.application.EliminarTipoVehiculo;
import mx.neology.acceso.application.GenerarInformePagos;
import mx.neology.acceso.application.IniciarMes;
import mx.neology.acceso.application.RegistrarEntrada;
import mx.neology.acceso.application.RegistrarSalida;
import mx.neology.acceso.application.RegistrarTipoVehiculo;
import mx.neology.acceso.application.RegistrarVehiculo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
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
      VehiculoRepositorio vehiculos,
      TipoVehiculoRepositorio tipos,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarVehiculo(vehiculos, tipos, transaccion, clock);
  }

  @Bean
  RegistrarEntrada registrarEntrada(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarEntrada(vehiculos, estancias, tipos, calculadora, transaccion, clock);
  }

  @Bean
  RegistrarSalida registrarSalida(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarSalida(
        vehiculos, estancias, periodos, acumulados, tipos, calculadora, transaccion, clock);
  }

  @Bean
  IniciarMes iniciarMes(PeriodoRepositorio periodos, Transaccion transaccion, Clock clock) {
    return new IniciarMes(periodos, transaccion, clock);
  }

  @Bean
  GenerarInformePagos generarInformePagos(
      VehiculoRepositorio vehiculos,
      PeriodoRepositorio periodos,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion) {
    return new GenerarInformePagos(vehiculos, periodos, tipos, calculadora, transaccion);
  }

  @Bean
  ConsultarVehiculos consultarVehiculos(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new ConsultarVehiculos(
        vehiculos, estancias, periodos, acumulados, tipos, calculadora, transaccion, clock);
  }

  @Bean
  ConsultarTiposVehiculo consultarTiposVehiculo(
      TipoVehiculoRepositorio tipos, VehiculoRepositorio vehiculos, Transaccion transaccion) {
    return new ConsultarTiposVehiculo(tipos, vehiculos, transaccion);
  }

  @Bean
  RegistrarTipoVehiculo registrarTipoVehiculo(
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new RegistrarTipoVehiculo(tipos, calculadora, transaccion, clock);
  }

  @Bean
  ActualizarTipoVehiculo actualizarTipoVehiculo(
      TipoVehiculoRepositorio tipos,
      VehiculoRepositorio vehiculos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    return new ActualizarTipoVehiculo(tipos, vehiculos, calculadora, transaccion, clock);
  }

  @Bean
  EliminarTipoVehiculo eliminarTipoVehiculo(
      TipoVehiculoRepositorio tipos, VehiculoRepositorio vehiculos, Transaccion transaccion) {
    return new EliminarTipoVehiculo(tipos, vehiculos, transaccion);
  }
}
