package api.estacionamiento.controller;

import api.estacionamiento.repository.IResidenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/neo")

@CrossOrigin(origins = "*")//para poder utilizar el frontend
public class ReporteController {

    @Autowired
    private IResidenteRepository iResidenteRepository;

    @GetMapping("/residentes/pagos")
    public List<Map<String, Object>> reporteResidentes(){
        return iResidenteRepository.findAll().stream()
                .map(r -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("placa", r.getPlaca());
                    datos.put("minutos", r.getTiempoAcumulado());
                    datos.put("monto", r.getTiempoAcumulado() * 0.05);
                    return datos;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/mes/iniciar")
    public void reiniciarMes() {
        iResidenteRepository.findAll().forEach(r -> {
            r.setTiempoAcumulado(0L);
            iResidenteRepository.save(r);
        });
    }
}