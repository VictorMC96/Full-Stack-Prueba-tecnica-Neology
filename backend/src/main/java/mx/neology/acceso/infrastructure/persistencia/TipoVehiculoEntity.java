package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import mx.neology.acceso.domain.ModeloCobro;

@Entity
@Table(name = "tipos_vehiculo")
public class TipoVehiculoEntity {

  @Id
  @Column(length = 20)
  private String codigo;

  @Column(nullable = false, length = 40)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(name = "modelo_cobro", nullable = false, length = 30)
  private ModeloCobro modeloCobro;

  @Column(name = "tarifa_por_minuto", nullable = false, precision = 12, scale = 2)
  private BigDecimal tarifaPorMinuto;

  @Column(nullable = false)
  private boolean provisional;

  @Column(nullable = false)
  private boolean integrado;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected TipoVehiculoEntity() {}

  public TipoVehiculoEntity(
      String codigo,
      String nombre,
      ModeloCobro modeloCobro,
      BigDecimal tarifaPorMinuto,
      boolean provisional,
      boolean integrado,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.modeloCobro = modeloCobro;
    this.tarifaPorMinuto = tarifaPorMinuto;
    this.provisional = provisional;
    this.integrado = integrado;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public ModeloCobro getModeloCobro() {
    return modeloCobro;
  }

  public BigDecimal getTarifaPorMinuto() {
    return tarifaPorMinuto;
  }

  public boolean isProvisional() {
    return provisional;
  }

  public boolean isIntegrado() {
    return integrado;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
