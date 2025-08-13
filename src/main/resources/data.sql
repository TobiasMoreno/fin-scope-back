
INSERT INTO permissions (id, description)
VALUES (1, 'READ_PRIVILEGE'),
       (2, 'WRITE_PRIVILEGE'),
       (3, 'DELETE_PRIVILEGE');

INSERT INTO roles (id, name)
VALUES (1, 'USER'),
       (2, 'ADMIN');

INSERT INTO roles_permissions (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2),
       (2, 3);

INSERT INTO users (email,
                   name,
                   picture,
                   provider,
                   created_at,
                   updated_at)
VALUES ('admin@example.com', 'Admin User', 'https://example.com/admin.jpg', 'google', NOW(), NOW()),
       ('user@example.com', 'Regular User', 'https://example.com/user.jpg', 'google', NOW(), NOW());

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2),
       (2, 1);

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
