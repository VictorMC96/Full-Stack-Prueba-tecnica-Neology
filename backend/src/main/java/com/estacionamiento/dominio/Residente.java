package com.estacionamiento.dominio;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "residentes")
public class Residente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", nullable = false, unique = true)
    private Vehiculo vehiculo;

    @Column(nullable = false)
    private long minutosAcumulados;

    private Residente(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
        this.minutosAcumulados = 0L;
    }

    public static Residente de(Vehiculo vehiculo) {
        return new Residente(vehiculo);
    }

    public void sumarMinutos(long minutos) {
        this.minutosAcumulados += minutos;
    }

    public void reiniciar() {
        this.minutosAcumulados = 0L;
    }
}
