package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "estancias")
public class EstanciaEntity {

  @Id private UUID id;

  @Column(name = "vehiculo_id", nullable = false)
  private UUID vehiculoId;

  @Column(nullable = false)
  private OffsetDateTime entrada;

  private OffsetDateTime salida;

  private Integer minutos;

  @Column(precision = 12, scale = 2)
  private BigDecimal importe;

  @Column(name = "periodo_id")
  private UUID periodoId;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected EstanciaEntity() {}

  public EstanciaEntity(
      UUID id,
      UUID vehiculoId,
      OffsetDateTime entrada,
      OffsetDateTime salida,
      Integer minutos,
      BigDecimal importe,
      UUID periodoId,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this.id = id;
    this.vehiculoId = vehiculoId;
    this.entrada = entrada;
    this.salida = salida;
    this.minutos = minutos;
    this.importe = importe;
    this.periodoId = periodoId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getVehiculoId() {
    return vehiculoId;
  }

  public OffsetDateTime getEntrada() {
    return entrada;
  }

  public OffsetDateTime getSalida() {
    return salida;
  }

  public Integer getMinutos() {
    return minutos;
  }

  public BigDecimal getImporte() {
    return importe;
  }

  public UUID getPeriodoId() {
    return periodoId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
