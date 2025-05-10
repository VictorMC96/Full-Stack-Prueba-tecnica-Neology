package api.estacionamiento.service;

import api.estacionamiento.models.Vehiculo;
import api.estacionamiento.repository.IVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {

    @Autowired
    private IVehiculoRepository iVehiculoRepository;

    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        return iVehiculoRepository.save(vehiculo);
    }
}