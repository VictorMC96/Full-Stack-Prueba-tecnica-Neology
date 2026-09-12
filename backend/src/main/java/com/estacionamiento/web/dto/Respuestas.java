package com.estacionamiento.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//Respuestas-API
public final class Respuestas {

    private Respuestas() {
    }

    public record Vehiculo(
            String placa,
            String tipo,
            String propietario,
            LocalDateTime alta,
            boolean dentro,
            long minutosAcumulados) {
    }

    public record Estancia(
            Long id,
            String placa,
            LocalDateTime entrada,
            LocalDateTime salida,
            Long minutos,
            BigDecimal monto,
            Integer periodo) {
    }

    public record DetalleVehiculo(Vehiculo vehiculo, List<Estancia> estancias) {
    }

    //Salida-Paginada
    public record Pagina<T>(List<T> contenido, int pagina, int tamano, long total, int totalPaginas) {
    }

    public record RenglonPago(String placa, String propietario, long minutos, BigDecimal monto) {
    }

    public record InformePagos(int periodo, List<RenglonPago> renglones, BigDecimal total) {
    }

    public record Mes(int periodo, String mensaje) {
    }

    //Salida-Error
    public record Error(String codigo, String mensaje, LocalDateTime momento) {
    }
}
