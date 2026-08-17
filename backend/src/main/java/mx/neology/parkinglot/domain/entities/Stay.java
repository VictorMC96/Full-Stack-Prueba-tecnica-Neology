package mx.neology.parkinglot.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.neology.parkinglot.domain.dtos.DetailStayDto;

/**
 * Estancia
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_stays")
public class Stay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "license_plate", unique = false, nullable = false, length = 10)
    private String licensePlate;

    @Column(name = "dt_entry_date")
    private LocalDateTime entryDate;

    @Column(name = "dt_exit_date")
    private LocalDateTime exitDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "st_is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_plate", referencedColumnName = "license_plate",
                insertable = false, updatable = false)
    private Vehicle vehicle;

    public DetailStayDto toDto() {
        return DetailStayDto.builder()
                .licensePlate(licensePlate)
                .entryDate(entryDate)
                .exitDate(exitDate)
                .build();
    }

    @Override
    public String toString() {
        return "Stay [id=" + id + ", licensePlate=" + licensePlate + ", entryDate=" + entryDate
                + ", exitDate=" + exitDate + ", createdAt=" + createdAt + ", createdBy=" + createdBy
                + ", updatedAt=" + updatedAt + ", updatedBy=" + updatedBy + ", isActive=" + isActive + "]";
    }

}