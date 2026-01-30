-- Datos base para funcionamiento didáctico (evita fallos por estados/roles inexistentes)

-- Roles
INSERT IGNORE INTO rol (nombre) VALUES ('CLIENTE'), ('ADMIN');

-- Estados de pedido (deben coincidir con lo que usa la capa service)
INSERT IGNORE INTO estado_pedido (nombre) VALUES
  ('CREADO'),
  ('VALIDANDO_PAGO'),
  ('PREPARANDO_PEDIDO'),
  ('EN_TRANSITO'),
  ('ENTREGADO'),
  ('CANCELADO');

-- Estados de soporte (deben coincidir con lo que usa la capa service)
INSERT IGNORE INTO estado_caso (nombre) VALUES
  ('ABIERTO'),
  ('EN_REVISION'),
  ('CERRADO');

-- Categorías de soporte sugeridas
INSERT IGNORE INTO categoria_soporte (nombre) VALUES
  ('ENVIO'),
  ('PAGO'),
  ('CUENTA');

