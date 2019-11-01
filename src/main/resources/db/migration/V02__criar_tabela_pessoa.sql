CREATE TABLE pessoa (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT, 
	nome VARCHAR(50) NOT NULL,
    logradouro VARCHAR(50),
	numero VARCHAR(10),   
    complemento VARCHAR(20),
    bairro VARCHAR(20),
    cep VARCHAR(10),
    cidade VARCHAR(20),
    estado VARCHAR(2),
    ativo BOOLEAN NOT NULL	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;	

INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Isaac Mateus Nascimento','Rua Jairo Andrade Macedo',222,'','São Conrado','49042-480','Aracaju','SE',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Simone Yasmin Eduarda das Neves','Rua Seis',845,'','Gurupi','64091-460','Teresina','PI',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Antonella Ana Adriana da Cruz','Rua Juceli Rodrigues',311,'','Ceará','88815-177','Criciúma','SC',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Eduardo Danilo Luiz Gonçalves','Avenida da Paz',320,'','Sussuarana','41214-325','Salvador','BA',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Lucas Lorenzo da Cruz','Rua Raul Hélio',743,'','Iraci','69101-083','Itacoatiara','AM',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Tânia Rayssa Duarte','Rua Dez de Setembro',719,'','Jardim Belval','06420-290','Barueri','SP',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Camila Teresinha Sales','Avenida Farquar',198,'','Olaria','76801-210','Porto Velho','RO',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Bárbara Lorena Rebeca da Cruz','Rua Apolo XI',628,'','Campinas de Pirajá','41275-480','Salvador','BA',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Yuri Severino Santos','Avenida dos Diamantes',954,'','Nova União 01','76875-661','Ariquemes','RO',true);
INSERT INTO pessoa (nome,logradouro,numero,complemento,bairro,cep,cidade,estado,ativo) VALUES ('Olivia Isabel Elaine Barros','Rua Roque Santeiro',212,'','Tijuca','94834-830','Alvorada','RS',true);