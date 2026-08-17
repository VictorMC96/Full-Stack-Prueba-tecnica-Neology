package mx.neology.parkinglot.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.neology.parkinglot.domain.dtos.DetailVehicleDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_vehicles")
public class Vehicle {

    // Placa
    @Id
    @Column(name = "license_plate", nullable = false, length = 10)
    private String licensePlate;

    @Column(name = "txt_type", length = 45, nullable = false)
    private String type;

    // --- Nuevos campos visibles a simple vista ---

    @Column(name = "txt_brand", length = 50)
    private String brand;        // Marca: Nissan, Toyota, VW...

    @Column(name = "txt_model", length = 50)
    private String model;        // Modelo: Versa, Corolla...

    @Column(name = "txt_color", length = 30)
    private String color;        // Color: Blanco, Rojo...

    @Column(name = "txt_body_type", length = 30)
    private String bodyType;     // Carrocería: sedán, SUV, pickup, hatchback...

    @Column(name = "num_year")
    private Integer year;        // Año aproximado

    @Column(name = "txt_country_plate", length = 40)
    private String countryPlate; // País/estado de la placa (visible en la placa)

    // --- Auditoría ---

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public DetailVehicleDto toDto() {
        return DetailVehicleDto.builder()
                .licensePlate(licensePlate)
                .type(type)
                .brand(brand)
                .model(model)
                .color(color)
                .bodyType(bodyType)
                .year(year)
                .countryPlate(countryPlate)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .isActive(isActive)
                .build();
    }

    @Override
    public String toString() {
        return "Vehicle [licensePlate=" + licensePlate + ", type=" + type + ", brand=" + brand
                + ", model=" + model + ", color=" + color + ", bodyType=" + bodyType + ", year=" + year
                + ", countryPlate=" + countryPlate + ", createdAt=" + createdAt + ", createdBy=" + createdBy
                + ", updatedAt=" + updatedAt + ", updatedBy=" + updatedBy + ", isActive=" + isActive + "]";
    }
}