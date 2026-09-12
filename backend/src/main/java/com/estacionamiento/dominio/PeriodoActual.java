package com.estacionamiento.dominio;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

//Contador-Periodo
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "periodo_actual")
public class PeriodoActual {

    @Id
    private Long id;

    @Column(nullable = false)
    private int numero;

    private PeriodoActual(long id, int numero) {
        this.id = id;
        this.numero = numero;
    }

    //Periodo-Inicial
    public static PeriodoActual inicial() {
        return new PeriodoActual(1L, 1);
    }

    //Avance-Periodo
    public void avanzar() {
        this.numero++;
    }
}
