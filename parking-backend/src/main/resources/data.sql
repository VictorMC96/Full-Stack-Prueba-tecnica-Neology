-- Datos de ejemplo para probar el sistema sin tener que dar de alta nada a mano.
-- Se carga automaticamente al arrancar (ver spring.sql.init.mode en application.properties).
-- Como spring.jpa.hibernate.ddl-auto=create-drop, esto se vuelve a insertar en cada arranque.

-- Vehiculos: uno de cada tipo, mas un segundo residente para variar el reporte
INSERT INTO vehiculos (placa, tipo) VALUES ('OFI001', 'OFICIAL');
INSERT INTO vehiculos (placa, tipo) VALUES ('RES100', 'RESIDENTE');
INSERT INTO vehiculos (placa, tipo) VALUES ('RES200', 'RESIDENTE');
INSERT INTO vehiculos (placa, tipo) VALUES ('VIS300', 'NO_RESIDENTE');

-- Residentes: RES100 ya tiene historial acumulado del mes; RES200 acaba de entrar y no acumula nada aun
INSERT INTO residentes (placa, tiempo_acumulado_minutos, pago_acumulado) VALUES ('RES100', 340, 17.00);
INSERT INTO residentes (placa, tiempo_acumulado_minutos, pago_acumulado) VALUES ('RES200', 0, 0.00);

-- Estancias
-- OFI001: entrada y salida ya cerradas, no paga por ser oficial
INSERT INTO estancias (placa_vehiculo, fecha_entrada, fecha_salida, cobro)
VALUES ('OFI001', '2026-08-14 08:00:00', '2026-08-14 08:45:00', 0.00);

-- RES100: dos estancias cerradas cuya suma (200 + 140 min, $10.00 + $7.00) coincide con lo acumulado arriba
INSERT INTO estancias (placa_vehiculo, fecha_entrada, fecha_salida, cobro)
VALUES ('RES100', '2026-08-12 09:00:00', '2026-08-12 12:20:00', 10.00);
INSERT INTO estancias (placa_vehiculo, fecha_entrada, fecha_salida, cobro)
VALUES ('RES100', '2026-08-13 09:00:00', '2026-08-13 11:20:00', 7.00);

-- RES200: estancia abierta (todavia esta en el estacionamiento)
INSERT INTO estancias (placa_vehiculo, fecha_entrada, fecha_salida, cobro)
VALUES ('RES200', '2026-08-14 07:30:00', NULL, 0.00);

-- VIS300: visitante que ya salio y pago su tarifa por minuto
INSERT INTO estancias (placa_vehiculo, fecha_entrada, fecha_salida, cobro)
VALUES ('VIS300', '2026-08-14 10:00:00', '2026-08-14 10:30:00', 15.00);
