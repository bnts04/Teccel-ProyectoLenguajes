INSERT INTO clientes (id, nombres, apellidos, telefono, dni, activo, fecha_creacion) VALUES (1, 'Carlos', 'Mendoza', '987654321', '77665544', true, NOW());

INSERT INTO equipos (id, marca, modelo, tipo, cliente_id, activo, codigo_interno, fecha_creacion) VALUES (1, 'Samsung', 'Galaxy S23', 'CELULAR', 1, true, 'EQ-001', NOW());

INSERT INTO ordenes_reparacion (id, ticket, equipo_id, tecnico_id, falla_reportada, precio_acordado, dias_estimados, fecha_ingreso, fecha_estimada_entrega, prioridad, estado, activo, fecha_creacion) VALUES (1, 'TC-102030', 1, 1, 'Pantalla rota totalmente', 150.00, 3, NOW(), CURRENT_DATE + INTERVAL '3 days', 1, 0, true, NOW());