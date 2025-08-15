-- Permisos del sistema
INSERT INTO permissions (id, description)
VALUES (1, 'READ_BASIC'),
       (2, 'WRITE_BASIC'),
       (3, 'DELETE_BASIC'),
       (4, 'READ_ADVANCED'),
       (5, 'WRITE_ADVANCED'),
       (6, 'DELETE_ADVANCED'),
       (7, 'ADMIN_ACCESS'),
       (8, 'USER_MANAGEMENT'),
       (9, 'SYSTEM_CONFIG');

-- Roles del sistema
INSERT INTO roles (id, name)
VALUES (1, 'DEFAULT'),
       (2, 'PREMIUM'),
       (3, 'ADMIN');

-- Asignación de permisos a roles
-- DEFAULT: Solo funcionalidades básicas
INSERT INTO roles_permissions (role_id, permission_id)
VALUES (1, 1),  -- READ_BASIC
       (1, 2);  -- WRITE_BASIC

-- PREMIUM: Funcionalidades básicas + avanzadas
INSERT INTO roles_permissions (role_id, permission_id)
VALUES (2, 1),  -- READ_BASIC
       (2, 2),  -- WRITE_BASIC
       (2, 3),  -- DELETE_BASIC
       (2, 4),  -- READ_ADVANCED
       (2, 5);  -- WRITE_ADVANCED

-- ADMIN: Acceso total
INSERT INTO roles_permissions (role_id, permission_id)
VALUES (3, 1),  -- READ_BASIC
       (3, 2),  -- WRITE_BASIC
       (3, 3),  -- DELETE_BASIC
       (3, 4),  -- READ_ADVANCED
       (3, 5),  -- WRITE_ADVANCED
       (3, 6),  -- DELETE_ADVANCED
       (3, 7),  -- ADMIN_ACCESS
       (3, 8),  -- USER_MANAGEMENT
       (3, 9);  -- SYSTEM_CONFIG

-- Usuario administrador
INSERT INTO users (email,
                   name,
                   picture,
                   provider,
                   created_at,
                   updated_at)
VALUES ('admin@finscope.com', 'Admin FinScope', 'https://example.com/admin.jpg', 'google', NOW(), NOW());

-- Asignar rol ADMIN al usuario administrador
INSERT INTO users_roles (user_id, role_id)
VALUES (1, 3);

-- Datos de ejemplo para transacciones
INSERT INTO transactions (asset_name,
                          asset_type,
                          tx_type,
                          quantity,
                          price_per_unit,
                          created_at,
                          updated_at)
VALUES ('AAPL', 'SHARE', 'BUY', 10.00000000, 150.25000000, NOW(), NOW()),
       ('AAPL', 'SHARE', 'BUY', 5.50000000, 155.75000000, NOW(), NOW()),
       ('AAPL', 'SHARE', 'SELL', 3.00000000, 158.00000000, NOW(), NOW()),

       ('BTC', 'CRYPTO', 'BUY', 0.10000000, 60000.00000000, NOW(), NOW()),
       ('BTC', 'CRYPTO', 'BUY', 0.05000000, 62000.00000000, NOW(), NOW()),
       ('BTC', 'CRYPTO', 'SELL', 0.02000000, 63000.00000000, NOW(), NOW()),

       ('ETH', 'CRYPTO', 'BUY', 1.25000000, 3200.00000000, NOW(), NOW()),
       ('ETH', 'CRYPTO', 'BUY', 0.75000000, 3300.00000000, NOW(), NOW()),
       ('ETH', 'CRYPTO', 'SELL', 0.50000000, 3400.00000000, NOW(), NOW());
