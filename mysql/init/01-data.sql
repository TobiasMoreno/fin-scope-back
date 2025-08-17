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

-- Datos de ejemplo para inversiones
INSERT INTO investments (user_id, symbol, name, quantity, last_price, daily_variation_percent, daily_variation_amount, cost_per_share, gain_loss_percent, gain_loss_amount, total, type, created_at, updated_at)
VALUES 
    -- CEDEARs
    (1, 'AAPL', 'CEDEAR APPLE INC', 10.000000, 15.50, 2.15, 0.33, 14.80, 4.73, 7.00, 155.00, 'CEDEAR', NOW(), NOW()),
    (1, 'MSFT', 'CEDEAR MICROSOFT CORP', 5.000000, 18.20, -1.08, -0.20, 17.50, 4.00, 3.50, 91.00, 'CEDEAR', NOW(), NOW()),
    (1, 'GOOGL', 'CEDEAR ALPHABET INC', 8.000000, 12.80, 0.78, 0.10, 12.20, 4.92, 4.80, 102.40, 'CEDEAR', NOW(), NOW()),
    
    -- Acciones
    (1, 'GGAL', 'GRUPO GALICIA', 100.000000, 0.85, -2.30, -0.02, 0.82, 3.66, 3.00, 85.00, 'ACCION', NOW(), NOW()),
    (1, 'YPF', 'YPF SA', 50.000000, 1.20, 1.69, 0.02, 1.15, 4.35, 2.50, 60.00, 'ACCION', NOW(), NOW()),
    
    -- Criptomonedas
    (1, 'BTC', 'BITCOIN', 0.050000, 65000.00, 3.20, 104.00, 60000.00, 8.33, 250.00, 3250.00, 'CRIPTO', NOW(), NOW()),
    (1, 'ETH', 'ETHEREUM', 2.000000, 3200.00, -1.50, -96.00, 3000.00, 6.67, 400.00, 6400.00, 'CRIPTO', NOW(), NOW()),
    (1, 'ADA', 'CARDANO', 1000.000000, 0.45, 5.50, 23.50, 0.40, 12.50, 50.00, 450.00, 'CRIPTO', NOW(), NOW());
