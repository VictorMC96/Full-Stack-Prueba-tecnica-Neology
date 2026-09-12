package com.estacionamiento.dominio;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vehiculos", indexes = @Index(name = "idx_vehiculo_placa", columnList = "placa", unique = true))
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Column(length = 120)
    private String propietario;

    @Column(nullable = false)
    private LocalDateTime alta;

    private Vehiculo(String placa, TipoVehiculo tipo, String propietario) {
        this.placa = placa;
        this.tipo = tipo;
        this.propietario = propietario;
        this.alta = LocalDateTime.now();
    }

    public static Vehiculo de(String placa, TipoVehiculo tipo, String propietario) {
        return new Vehiculo(placa, tipo, propietario);
    }
}
