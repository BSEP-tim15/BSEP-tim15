INSERT INTO roles VALUES (1, 'ROLE_ORGANIZATION'); --root
INSERT INTO roles VALUES (2, 'ROLE_ADMIN');
INSERT INTO roles VALUES (3, 'ROLE_USER'); --ee
INSERT INTO roles VALUES (4, 'ROLE_SERVICE'); --intermediate

INSERT INTO permissions VALUES (1, 'read_certificate');
INSERT INTO permissions VALUES (2, 'create_certificate');
INSERT INTO permissions VALUES (3, 'revoke_certificate');
INSERT INTO permissions VALUES (4, 'get_issuers');

INSERT INTO roles_permissions VALUES (2,1); --admin read
INSERT INTO roles_permissions VALUES (2,2); --admin create
INSERT INTO roles_permissions VALUES (2,3); --admin revoke
INSERT INTO roles_permissions VALUES (2,4); --adm iss
INSERT INTO roles_permissions VALUES (1,1); --org read
INSERT INTO roles_permissions VALUES (1,2); --org create
INSERT INTO roles_permissions VALUES (1,3); --org revoke
INSERT INTO roles_permissions VALUES (1,4); --org iss
INSERT INTO roles_permissions VALUES (4,1); --serv read
INSERT INTO roles_permissions VALUES (4,2); --serv create
INSERT INTO roles_permissions VALUES (4,3); --serv revoke
INSERT INTO roles_permissions VALUES (4,4); --serv iss
INSERT INTO roles_permissions VALUES (3,1); --user read
