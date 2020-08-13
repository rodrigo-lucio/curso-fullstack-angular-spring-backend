ALTER TABLE algamoneyapi.usuario_permissao 
DROP FOREIGN KEY usuario_permissao_ibfk_1;

ALTER TABLE algamoneyapi.usuario 
CHANGE COLUMN codigo codigo BIGINT(20) NOT NULL AUTO_INCREMENT ;

ALTER TABLE algamoneyapi.usuario_permissao 
ADD CONSTRAINT usuario_permissao_ibfk_1
  FOREIGN KEY (codigo_usuario)
  REFERENCES algamoneyapi.usuario (codigo)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;
