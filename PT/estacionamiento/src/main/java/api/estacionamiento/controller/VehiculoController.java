package api.estacionamiento.controller;

import api.estacionamiento.models.Residente;
import api.estacionamiento.models.TipoVehiculo;
import api.estacionamiento.models.Vehiculo;

import api.estacionamiento.repository.IResidenteRepository;
import api.estacionamiento.repository.IVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//
import api.estacionamiento.dto.PlacaDTO;

@RestController
@RequestMapping("/neo/vehiculos")//ruta base
@CrossOrigin(origins = "*")//para poder utilizar el frontend
public class VehiculoController {

    @Autowired private IVehiculoRepository iVehiculoRepository;
    @Autowired private IResidenteRepository iResidenteRepository;

    /*
    @PostMapping("/oficiales")
    public void altaOficial(@RequestBody String placa) {
        iVehiculoRepository.save(new Vehiculo(placa, TipoVehiculo.OFICIAL));
    }

     */

    @PostMapping("/oficiales")
    public void altaOficial(@RequestBody PlacaDTO dto) {
        iVehiculoRepository.save(new Vehiculo(dto.getPlaca(), TipoVehiculo.OFICIAL));
    }

    @PostMapping("/residentes")
    public void altaResidente(@RequestBody PlacaDTO dto) {
        String placa = dto.getPlaca();
        iVehiculoRepository.save(new Vehiculo(placa, TipoVehiculo.RESIDENTE));
        iResidenteRepository.save(new Residente(placa, 0L));
    }
}