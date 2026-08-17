package mx.neology.parkinglot.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.neology.parkinglot.domain.dtos.DetailSpotDto;

/**
 * Lugar de estacionamiento (spot).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_spots")
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "license_plate", length = 10)
    private String licensePlate;

    @Column(name = "txt_code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "txt_status", nullable = false, length = 20)
    private String status;

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

    public DetailSpotDto toDto() {
        return DetailSpotDto.builder()
                .id(id)
                .licensePlate(licensePlate)
                .code(code)
                .status(status)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .isActive(isActive)
                .build();
    }

    @Override
    public String toString() {
        return "Spot [id=" + id + ", code=" + code + ", status=" + status
                + ", createdAt=" + createdAt + ", createdBy=" + createdBy
                + ", updatedAt=" + updatedAt + ", updatedBy=" + updatedBy
                + ", isActive=" + isActive + "]";
    }
}