package com.neology.parking_backend.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "residentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Residente {

    @Id
    private String placa;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "placa", referencedColumnName = "placa")
    private Vehiculo vehiculo;

    @Column(nullable = false)
    private Long tiempoAcumuladoMinutos;

    @Column(nullable = false)
    private BigDecimal pagoAcumulado;

    @PrePersist
    public void prePersist(){
        if (tiempoAcumuladoMinutos == null) tiempoAcumuladoMinutos = 0L;
        if (pagoAcumulado == null) pagoAcumulado = BigDecimal.ZERO;

    }


}
