package com.neology.parking_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.neology.parking_backend.model.Residente;

public interface ResidenteRepository extends JpaRepository<Residente, String>{

}
