CREATE TABLE permissions
(
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description   VARCHAR(255)
);

CREATE TABLE roles
(
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(255)
);

CREATE TABLE roles_permissions
(
    role_id       BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE
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
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);


INSERT INTO permissions (permission_id, description)
VALUES (1, 'READ');
INSERT INTO permissions (permission_id, description)
VALUES (2, 'WRITE');
INSERT INTO permissions (permission_id, description)
VALUES (3, 'DELETE');

INSERT INTO roles (role_id, name)
VALUES (1, 'ADMIN');
INSERT INTO roles (role_id, name)
VALUES (2, 'USER');

INSERT INTO roles_permissions (role_id, permission_id)
VALUES (1, 1);
INSERT INTO roles_permissions (role_id, permission_id)
VALUES (1, 2);
INSERT INTO roles_permissions (role_id, permission_id)
VALUES (1, 3);

INSERT INTO roles_permissions (role_id, permission_id)
VALUES (2, 1);

INSERT INTO users ( email, password,
                   is_enabled, is_account_not_expired, is_account_not_locked, is_credential_not_expired,
                   created_at, updated_at)
VALUES ( 'admin@fin.com', '$2a$10$EjemploFakeHasheado1234567890',
        true, true, true, true,
        NOW(), NOW());

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);
