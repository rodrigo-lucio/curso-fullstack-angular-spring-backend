## 💻 Projeto

Back-end do projeto desenvolvido durante o curso Fullstack Angular e Spring da [AlgaWorks](https://github.com/algaworks).

A API foi desenvolvida em Java 8.

O repositório do front-end você encontra [aqui](https://github.com/rodrigo-lucio/curso-fullstack-angular-spring-frontend).

## :rocket: Tecnologias

Neste projeto foram utilizadas as seguintes tecnologias:

- [Spring](https://spring.io/)
- [MySQL](https://www.mysql.com/)
- [Flyway](https://flywaydb.org/)  
- [Thymeleaf](https://www.thymeleaf.org/)
- [JasperReports](https://community.jaspersoft.com/)
- [Amazon S3](https://aws.amazon.com/pt/s3/)
- Autenticação OAuth2 utilizando [JWT](https://jwt.io/)
- Deploy no [Heroku](https://heroku.com/)


## :pencil: IDE

Utilizado Spring Tool Suite 4.

## ▶️ Utilização

Importe o projeto para a sua IDE de preferência.

Tenha o MySQL instalado (Versão utilizada: 8.0.17).

Altere as configurações do banco de dados, e-mail e credenciais do S3 no arquivo [`/src/main/resources/application.properties`](https://github.com/rodrigo-lucio/curso-fullstack-angular-spring-backend/blob/master/src/main/resources/application.properties)

A estrutura da base de dados e o bucket do S3 serão criados automaticamente.

A API estará rodando em `http://localhost:8080`

## :closed_lock_with_key: Autenticação

Para autenticar, você pode enviar um POST com as seguintes credenciais para receber um token JWT: 

(As credencias estão armazenadas na tabela `usuario` do BD)

```
$  curl --user angular:@ngul@r0 -d "client=angular&username=admin@algamoney.com&password=admin&grant_type=password" -H "Content-Type: application/x-www-form-urlencoded" -X POST http://localhost:8080/oauth/token
```
A resposta deve ser assim:
```
{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJub21lIjoiQWRtaW5pc3RyYWRvciIsImV4cCI6MTU5NTk5MTczMCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DQURBU1RSQVJfQ0FURUdPUklBIiwiUk9MRV9QRVNRVUlTQVJfUEVTU09BIiwiUk9MRV9SRU1PVkVSX1BFU1NPQSIsIlJPTEVfQ0FEQVNUUkFSX0xBTkNBTUVOVE8iLCJST0xFX1BFU1FVSVNBUl9MQU5DQU1FTlRPIiwiUk9MRV9SRU1PVkVSX0xBTkNBTUVOVE8iLCJST0xFX0NBREFTVFJBUl9QRVNTT0EiLCJST0xFX1BFU1FVSVNBUl9DQVRFR09SSUEiXSwianRpIjoiOTg0ZDM0YTItZDFlOC00ZTVkLTk1OGEtMGMwZmU1YjViZGI4IiwiY2xpZW50X2lkIjoiYW5ndWxhciJ9.JWgSGERT4umuUpMs8IJALtJKV3qogx3_tXWqZTHzVX8","token_type":"bearer","expires_in":299,"scope":"read write","nome":"Administrador","jti":"984d34a2-d1e8-4e5d-958a-0c0fe5b5bdb8"}
```

Agora você pode inserir esse token no cabeçalho para enviar as requisições para a API, por exemplo:
```
$ curl -H 'Accept: application/json' -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJub21lIjoiQWRtaW5pc3RyYWRvciIsImV4cCI6MTU5NTk5MTczMCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DQURBU1RSQVJfQ0FURUdPUklBIiwiUk9MRV9QRVNRVUlTQVJfUEVTU09BIiwiUk9MRV9SRU1PVkVSX1BFU1NPQSIsIlJPTEVfQ0FEQVNUUkFSX0xBTkNBTUVOVE8iLCJST0xFX1BFU1FVSVNBUl9MQU5DQU1FTlRPIiwiUk9MRV9SRU1PVkVSX0xBTkNBTUVOVE8iLCJST0xFX0NBREFTVFJBUl9QRVNTT0EiLCJST0xFX1BFU1FVSVNBUl9DQVRFR09SSUEiXSwianRpIjoiOTg0ZDM0YTItZDFlOC00ZTVkLTk1OGEtMGMwZmU1YjViZGI4IiwiY2xpZW50X2lkIjoiYW5ndWxhciJ9.JWgSGERT4umuUpMs8IJALtJKV3qogx3_tXWqZTHzVX8" -X GET http://localhost:8080/lancamentos?resumo
```


