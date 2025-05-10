package api.estacionamiento.models;

import jakarta.persistence.*;

//import lombok.Data;//nos facilita los getter an setters
@Entity
public class Vehiculo {
    @Id
    private String placa;

    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipo;


    // Constructor vacío (requerido por JPA)
    public Vehiculo() {}

    // Constructor con parámetros
    public Vehiculo(String placa, TipoVehiculo tipo) {
        this.placa = placa;
        this.tipo = tipo;
    }

    // Getters
    public String getPlaca() {
        return placa;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    // Setters
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }
}