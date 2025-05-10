package api.estacionamiento.models;


import jakarta.persistence.*;


@Entity
public class Residente {
    @Id
    private String placa;

    private Long tiempoAcumulado; // en minutos


    // Constructor vacío requerido por JPA
    public Residente() {}

    // Constructor con parámetros (opcional)
    public Residente(String placa, Long tiempoAcumulado) {
        this.placa = placa;
        this.tiempoAcumulado = tiempoAcumulado;
    }

    // Getters
    public String getPlaca() {
        return placa;
    }

    public Long getTiempoAcumulado() {
        return tiempoAcumulado;
    }

    // Setters
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setTiempoAcumulado(Long tiempoAcumulado) {
        this.tiempoAcumulado = tiempoAcumulado;
    }
}