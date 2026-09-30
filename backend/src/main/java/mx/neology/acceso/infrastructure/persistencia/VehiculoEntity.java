package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.domain.TipoVehiculo;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "vehiculos")
public class VehiculoEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 10)
  private String placa;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoVehiculo tipo;

  @Column(name = "estancia_abierta_id")
  private UUID estanciaAbiertaId;

  /** Derivado de solo lectura: permite ordenar el listado por "vehículos dentro primero". */
  @Formula("estancia_abierta_id is not null")
  private Boolean dentro;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected VehiculoEntity() {}

  public VehiculoEntity(
      UUID id,
      String placa,
      TipoVehiculo tipo,
      UUID estanciaAbiertaId,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this.id = id;
    this.placa = placa;
    this.tipo = tipo;
    this.estanciaAbiertaId = estanciaAbiertaId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getPlaca() {
    return placa;
  }

  public TipoVehiculo getTipo() {
    return tipo;
  }

  public UUID getEstanciaAbiertaId() {
    return estanciaAbiertaId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
