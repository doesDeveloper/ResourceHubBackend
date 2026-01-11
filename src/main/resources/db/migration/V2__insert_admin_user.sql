INSERT INTO users (id, email, username, full_name, password, role, bookmarks)
VALUES (
    nextval('users_id_seq'),
    'dani@steam.com',
    'super',
    'System Administrator',
    '$2a$12$LHuBnuSosRLRkUyCwEEqXONha87lBxfVoe222.lJkzdvniotomjny',
    1,
    '{}'
);