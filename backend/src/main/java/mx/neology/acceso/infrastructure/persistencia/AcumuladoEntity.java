package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "acumulados_residentes")
public class AcumuladoEntity {

  @Id private UUID id;

  @Column(name = "vehiculo_id", nullable = false)
  private UUID vehiculoId;

  @Column(name = "periodo_id", nullable = false)
  private UUID periodoId;

  @Column(nullable = false)
  private Integer minutos;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal importe;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected AcumuladoEntity() {}

  public AcumuladoEntity(
      UUID id,
      UUID vehiculoId,
      UUID periodoId,
      Integer minutos,
      BigDecimal importe,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this.id = id;
    this.vehiculoId = vehiculoId;
    this.periodoId = periodoId;
    this.minutos = minutos;
    this.importe = importe;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getVehiculoId() {
    return vehiculoId;
  }

  public UUID getPeriodoId() {
    return periodoId;
  }

  public Integer getMinutos() {
    return minutos;
  }

  public BigDecimal getImporte() {
    return importe;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
