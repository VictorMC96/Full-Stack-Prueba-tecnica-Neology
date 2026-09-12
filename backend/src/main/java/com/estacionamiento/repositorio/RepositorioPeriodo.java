package com.estacionamiento.repositorio;

import com.estacionamiento.dominio.PeriodoActual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPeriodo extends JpaRepository<PeriodoActual, Long> {
}
