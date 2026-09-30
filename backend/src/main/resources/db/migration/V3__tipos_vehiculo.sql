CREATE TABLE tipos_vehiculo (
    codigo            VARCHAR(20) PRIMARY KEY,
    nombre            VARCHAR(40) NOT NULL,
    modelo_cobro      VARCHAR(30) NOT NULL,
    tarifa_por_minuto DECIMAL(12, 2) NOT NULL,
    provisional       BOOLEAN NOT NULL,
    integrado         BOOLEAN NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL
);

INSERT INTO tipos_vehiculo (codigo, nombre, modelo_cobro, tarifa_por_minuto, provisional, integrado, created_at, updated_at) VALUES
    ('OFICIAL', 'Oficial', 'SIN_COBRO', 0.00, FALSE, TRUE,
     TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00'),
    ('RESIDENTE', 'Residente', 'POR_MINUTO_ACUMULADO', 0.05, FALSE, TRUE,
     TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00'),
    ('NO_RESIDENTE', 'No residente', 'POR_MINUTO_AL_SALIR', 0.50, TRUE, TRUE,
     TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00');

ALTER TABLE vehiculos
    ADD CONSTRAINT fk_vehiculos_tipo FOREIGN KEY (tipo) REFERENCES tipos_vehiculo (codigo);
