CREATE TABLE permissions
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE roles
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE roles_permissions
(
    role_id       BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE users
(
    id                        BIGINT PRIMARY KEY AUTO_INCREMENT,
    email                     VARCHAR(255) NOT NULL UNIQUE,
    password                  VARCHAR(255),
    is_enabled                BOOLEAN,
    is_account_not_expired    BOOLEAN,
    is_account_not_locked     BOOLEAN,
    is_credential_not_expired BOOLEAN,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP
);

CREATE TABLE users_roles
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE transactions
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    asset_name     VARCHAR(10),
    asset_type     VARCHAR(10)    NOT NULL CHECK (asset_type IN ('CRYPTO', 'SHARE')),
    tx_type        VARCHAR(10)    NOT NULL CHECK (tx_type IN ('BUY', 'SELL')),
    quantity       NUMERIC(20, 8) NOT NULL,
    price_per_unit NUMERIC(18, 8) NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);


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
                   password,
                   is_enabled,
                   is_account_not_expired,
                   is_account_not_locked,
                   is_credential_not_expired)
VALUES ('admin@example.com', '$2a$10$AdminHashedPasswordHere', true, true, true, true),
       ('user@example.com', '$2a$10$UserHashedPasswordHere', true, true, true, true);

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
