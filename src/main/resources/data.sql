-- Жанры
DELETE FROM genres;
INSERT INTO genres (id, name) VALUES (1, 'COMEDY');
INSERT INTO genres (id, name) VALUES (2, 'DRAMA');
INSERT INTO genres (id, name) VALUES (3, 'CARTOON');
INSERT INTO genres (id, name) VALUES (4, 'THRILLER');
INSERT INTO genres (id, name) VALUES (5, 'DOCUMENTARY');
INSERT INTO genres (id, name) VALUES (6, 'ACTION');

-- Рейтинги MPA
INSERT INTO mpa (id, name) VALUES (1, 'G');
INSERT INTO mpa (id, name) VALUES (2, 'PG');
INSERT INTO mpa (id, name) VALUES (3, 'PG-13');
INSERT INTO mpa (id, name) VALUES (4, 'R');
INSERT INTO mpa (id, name) VALUES (5, 'NC-17');