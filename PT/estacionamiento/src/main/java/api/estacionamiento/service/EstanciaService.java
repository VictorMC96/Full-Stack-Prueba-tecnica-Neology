package api.estacionamiento.service;



import api.estacionamiento.models.Estancia;
import api.estacionamiento.models.Residente;
import api.estacionamiento.models.TipoVehiculo;
import api.estacionamiento.models.Vehiculo;
import api.estacionamiento.repository.IEstanciaRepository;
import api.estacionamiento.repository.IResidenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import api.estacionamiento.repository.IVehiculoRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class EstanciaService {

    @Autowired private IVehiculoRepository iVehiculoRepository;
    @Autowired private IEstanciaRepository iEstanciaRepository;
    @Autowired private IResidenteRepository iResidenteRepository;

    public void registrarEntrada (String placa){
        Estancia e = new Estancia();
        e.setPlaca(placa);
        e.setFechaHoraEntrada(LocalDateTime.now());
        iEstanciaRepository.save(e);
    }

    public BigDecimal registrarSalida(String placa) {
        Estancia estancia = iEstanciaRepository.findByPlaca(placa)
                .stream()
                .filter(e -> e.getFechaHoraSalida() == null)
                .findFirst()
                .orElseThrow();

        estancia.setFechaHoraSalida(LocalDateTime.now());
        iEstanciaRepository.save(estancia);

        Vehiculo vehiculo = iVehiculoRepository.findById(placa).orElseThrow();
        long minutos = Duration.between(estancia.getFechaHoraEntrada(), estancia.getFechaHoraSalida()).toMinutes();

        if (vehiculo.getTipo() == TipoVehiculo.OFICIAL) return BigDecimal.ZERO;
        if (vehiculo.getTipo() == TipoVehiculo.RESIDENTE) {
            Residente r = iResidenteRepository.findById(placa).orElseThrow();
            r.setTiempoAcumulado(r.getTiempoAcumulado() + minutos);
            iResidenteRepository.save(r);
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(minutos * 0.5);
    }
}