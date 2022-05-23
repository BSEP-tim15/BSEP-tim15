INSERT INTO roles VALUES (1, 'organization');
INSERT INTO roles VALUES (2, 'admin');
INSERT INTO roles VALUES (3, 'user');
INSERT INTO roles VALUES (4, 'service');

INSERT INTO permissions VALUES (1, 'read_certificate');
INSERT INTO permissions VALUES (2, 'create_root_certificate');
INSERT INTO permissions VALUES (3, 'create_intermediate_certificate');
INSERT INTO permissions VALUES (4, 'create_end_entity_certificate');
INSERT INTO permissions VALUES (5, 'revoke_certificate');

INSERT INTO roles_permissions VALUES (2,1);
INSERT INTO roles_permissions VALUES (2,2);
INSERT INTO roles_permissions VALUES (2,3);
INSERT INTO roles_permissions VALUES (2,4);
INSERT INTO roles_permissions VALUES (2,5);


--INSERT INTO users VALUES (1, 'Srbija', 'pavkovicn@hotmail.com', true, 'root', 'root123', 'root', '');