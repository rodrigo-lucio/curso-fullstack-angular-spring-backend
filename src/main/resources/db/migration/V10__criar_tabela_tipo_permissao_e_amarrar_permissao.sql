CREATE TABLE tipo_permissao (
  codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  descricao VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO tipo_permissao (codigo, descricao) VALUES (1, 'Lançamento');
INSERT INTO tipo_permissao (codigo, descricao) VALUES (2, 'Pessoa');
INSERT INTO tipo_permissao (codigo, descricao) VALUES (3, 'Categoria');
INSERT INTO tipo_permissao (codigo, descricao) VALUES (4, 'Usuário');

ALTER TABLE permissao ADD COLUMN tipo_permissao integer;

UPDATE permissao SET tipo_permissao = 1 WHERE codigo in (6, 7, 8);
UPDATE permissao SET tipo_permissao = 2 WHERE codigo in (3, 4, 5);
UPDATE permissao SET tipo_permissao = 3 WHERE codigo in (1, 2);
UPDATE permissao SET tipo_permissao = 4 WHERE codigo in (9, 10, 11);