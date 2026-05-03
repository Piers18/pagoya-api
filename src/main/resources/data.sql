INSERT INTO roles (id, name) VALUES (1, 'ADMIN')    ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'CUSTOMER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (3, 'MERCHANT') ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('roles', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM roles)
);
