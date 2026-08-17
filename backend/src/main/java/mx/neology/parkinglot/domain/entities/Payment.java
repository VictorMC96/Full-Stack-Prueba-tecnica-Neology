package mx.neology.parkinglot.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.neology.parkinglot.domain.dtos.DetailPaymentDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "num_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "dt_payment_date")
    private LocalDateTime paymentDate;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stay_id", referencedColumnName = "id")
    private Stay stay;

    public DetailPaymentDto toDto() {
        return DetailPaymentDto.builder()
                .id(id)
                .amount(amount)
                .paymentDate(paymentDate)
                .status(status)
                .stayId(stay != null ? stay.getId() : null)
                .build();
    }

    @Override
    public String toString() {
        return "Payment [id=" + id + ", amount=" + amount + ", paymentDate=" + paymentDate + ", status=" + status
                + ", createdAt=" + createdAt + ", createdBy=" + createdBy + ", updatedAt=" + updatedAt + ", updatedBy="
                + updatedBy + ", isActive=" + isActive + ", stay=" + stay + "]";
    }

    
}