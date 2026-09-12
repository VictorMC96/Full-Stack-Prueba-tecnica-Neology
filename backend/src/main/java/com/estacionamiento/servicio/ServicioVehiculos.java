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

//Servicio-Vehiculos
@Service
@RequiredArgsConstructor
public class ServicioVehiculos {

    private final RepositorioVehiculo repositorioVehiculo;
    private final RepositorioResidente repositorioResidente;

    //Alta-Vehiculo
    @Transactional
    public Vehiculo alta(String placa, TipoVehiculo tipo, String propietario) {
        String normalizada = Placas.normalizar(placa);
        //Revision-Duplicado
        if (repositorioVehiculo.existsByPlaca(normalizada)) {
            throw new ReglaDeNegocio("La placa " + normalizada + " ya esta registrada");
        }
        Vehiculo vehiculo = repositorioVehiculo.save(Vehiculo.de(normalizada, tipo, propietario));
        //Creacion-Acumulado
        if (tipo == TipoVehiculo.RESIDENTE) {
            repositorioResidente.save(Residente.de(vehiculo));
        }
        return vehiculo;
    }

    //Busqueda-Placa
    @Transactional(readOnly = true)
    public Vehiculo buscar(String placa) {
        String normalizada = Placas.normalizar(placa);
        return repositorioVehiculo.findByPlaca(normalizada)
                .orElseThrow(() -> new RecursoNoEncontrado("No existe el vehiculo con placa " + normalizada));
    }

    //Listado-Filtrado
    @Transactional(readOnly = true)
    public Page<Vehiculo> listar(String filtroPlaca, Pageable pageable) {
        String filtro = Placas.limpiar(filtroPlaca);
        return repositorioVehiculo.findByPlacaContainingIgnoreCase(filtro, pageable);
    }

    //Acumulado-Residente
    @Transactional(readOnly = true)
    public long minutosAcumulados(Vehiculo vehiculo) {
        return repositorioResidente.findByVehiculo(vehiculo)
                .map(residente -> residente.getMinutosAcumulados())
                .orElse(0L);
    }
}
