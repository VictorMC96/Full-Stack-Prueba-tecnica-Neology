package mx.neology.parkinglot.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_residents")
public class Resident {

    @Id
    @Column(name = "license_plate", nullable = false, length = 7)
    private String licensePlate;

    @Column(name = "num_accumulated_time")
    private Long accumulatedTime;

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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "license_plate")
    private Vehicle vehicle;
}