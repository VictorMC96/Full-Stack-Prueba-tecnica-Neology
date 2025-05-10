package api.estacionamiento.dto;

public class PlacaDTO {
    private String placa;

    public PlacaDTO() {
    }

    public PlacaDTO(String placa) {
        this.placa = placa;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
