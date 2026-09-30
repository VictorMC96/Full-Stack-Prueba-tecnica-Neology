package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "periodos")
public class PeriodoEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private OffsetDateTime inicio;

  private OffsetDateTime fin;

  private Boolean activo;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected PeriodoEntity() {}

  public PeriodoEntity(
      UUID id,
      OffsetDateTime inicio,
      OffsetDateTime fin,
      Boolean activo,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this.id = id;
    this.inicio = inicio;
    this.fin = fin;
    this.activo = activo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public OffsetDateTime getInicio() {
    return inicio;
  }

  public OffsetDateTime getFin() {
    return fin;
  }

  public Boolean getActivo() {
    return activo;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
