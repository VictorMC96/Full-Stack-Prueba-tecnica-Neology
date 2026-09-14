package com.neo.parking;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "vehichles")
@AllArgsConstructor 
@NoArgsConstructor 
public class Vehicle {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plate", nullable = false, unique = true)
    @NotEmpty 
    private String plate;
    
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;
}
