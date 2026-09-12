package com.estacionamiento.dominio;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "estancias")
public class Estancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime salida;

    private Long minutos;

    @Column(precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private Integer periodo;

    private Estancia(Vehiculo vehiculo, LocalDateTime entrada, int periodo) {
        this.vehiculo = vehiculo;
        this.entrada = entrada;
        this.periodo = periodo;
    }

    public static Estancia abrir(Vehiculo vehiculo, LocalDateTime entrada, int periodo) {
        return new Estancia(vehiculo, entrada, periodo);
    }

    public void cerrar(LocalDateTime salida, long minutos, BigDecimal monto) {
        this.salida = salida;
        this.minutos = minutos;
        this.monto = monto;
    }

    public boolean estaAbierta() {
        return salida == null;
    }
}
