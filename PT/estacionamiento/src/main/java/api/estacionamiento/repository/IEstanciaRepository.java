package api.estacionamiento.repository;

import api.estacionamiento.models.Estancia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEstanciaRepository extends JpaRepository<Estancia, Long> {
    List<Estancia> findByPlaca(String placa);
}