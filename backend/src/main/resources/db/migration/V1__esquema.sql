CREATE TABLE periodos (
    id         UUID PRIMARY KEY,
    inicio     TIMESTAMP WITH TIME ZONE NOT NULL,
    fin        TIMESTAMP WITH TIME ZONE,
    activo     BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_periodos_activo CHECK (activo IS NULL OR activo = TRUE),
    CONSTRAINT uq_periodos_activo UNIQUE (activo)
);

CREATE TABLE vehiculos (
    id                  UUID PRIMARY KEY,
    placa               VARCHAR(10) NOT NULL,
    tipo                VARCHAR(20) NOT NULL,
    estancia_abierta_id UUID,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_vehiculos_placa UNIQUE (placa),
    CONSTRAINT uq_vehiculos_estancia_abierta UNIQUE (estancia_abierta_id)
    -- Sin CHECK sobre tipo: el valor lo garantiza el enum TipoVehiculo del dominio, y asi agregar
    -- un tipo de vehiculo no exige una migracion nueva.
);

CREATE TABLE estancias (
    id          UUID PRIMARY KEY,
    vehiculo_id UUID NOT NULL,
    entrada     TIMESTAMP WITH TIME ZONE NOT NULL,
    salida      TIMESTAMP WITH TIME ZONE,
    minutos     INT,
    importe     DECIMAL(12, 2),
    periodo_id  UUID,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_estancias_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_estancias_periodo FOREIGN KEY (periodo_id) REFERENCES periodos (id)
);

CREATE INDEX idx_estancias_vehiculo_id ON estancias (vehiculo_id);
CREATE INDEX idx_estancias_periodo_id ON estancias (periodo_id);
CREATE INDEX idx_estancias_vehiculo_entrada ON estancias (vehiculo_id, entrada DESC);

ALTER TABLE vehiculos
    ADD CONSTRAINT fk_vehiculos_estancia_abierta
        FOREIGN KEY (estancia_abierta_id) REFERENCES estancias (id);

CREATE TABLE acumulados_residentes (
    id          UUID PRIMARY KEY,
    vehiculo_id UUID NOT NULL,
    periodo_id  UUID NOT NULL,
    minutos     INT NOT NULL DEFAULT 0,
    importe     DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_acumulados_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_acumulados_periodo FOREIGN KEY (periodo_id) REFERENCES periodos (id),
    CONSTRAINT uq_acumulados_vehiculo_periodo UNIQUE (vehiculo_id, periodo_id)
);

CREATE INDEX idx_acumulados_periodo_id ON acumulados_residentes (periodo_id);
