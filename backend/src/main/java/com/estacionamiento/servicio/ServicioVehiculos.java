package com.estacionamiento.servicio;

import com.estacionamiento.dominio.Residente;
import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.dominio.Vehiculo;
import com.estacionamiento.error.RecursoNoEncontrado;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.repositorio.RepositorioResidente;
import com.estacionamiento.repositorio.RepositorioVehiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioVehiculos {

    private final RepositorioVehiculo repositorioVehiculo;
    private final RepositorioResidente repositorioResidente;

    @Transactional
    public Vehiculo alta(String placa, TipoVehiculo tipo, String propietario) {
        String normalizada = Placas.normalizar(placa);
        if (repositorioVehiculo.existsByPlaca(normalizada)) {
            throw new ReglaDeNegocio("La placa " + normalizada + " ya esta registrada");
        }
        Vehiculo vehiculo = repositorioVehiculo.save(Vehiculo.de(normalizada, tipo, propietario));
        if (tipo == TipoVehiculo.RESIDENTE) {
            repositorioResidente.save(Residente.de(vehiculo));
        }
        return vehiculo;
    }

    @Transactional(readOnly = true)
    public Vehiculo buscar(String placa) {
        String normalizada = Placas.normalizar(placa);
        return repositorioVehiculo.findByPlaca(normalizada)
                .orElseThrow(() -> new RecursoNoEncontrado("No existe el vehiculo con placa " + normalizada));
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> listar(String filtroPlaca, Pageable pageable) {
        String filtro = filtroPlaca == null ? "" : filtroPlaca.trim().toUpperCase().replace(" ", "").replace("-", "");
        return repositorioVehiculo.findByPlacaContainingIgnoreCase(filtro, pageable);
    }
}
