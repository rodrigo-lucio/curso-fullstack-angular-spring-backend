package com.example.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * Classe responsável por configurar o ambiente de produção
 * Chamamos esses atributos nas classes onde precisamos setar true quando estiver rodando em produção
 * Efetuamos as configurações no arquivo de profile application-prod.properties
 */

@Component
@ConfigurationProperties("algamoney")			//habilita essa classe para ser seus atributos serem acessiveis nos arquivos .properties
public class AlgamoneyApiProperty {

	//16/04/2020 - Foi alterado para 4200, analizar depois o que houve
	//private String originPermitida = "https://result-angular.herokuapp.com";
	private String originPermitida;

	private final Seguranca seguranca = new Seguranca();
	
	private final Mail mail = new Mail();

	private final S3 s3 = new S3();
	
	public S3 getS3(){
		return s3;
	}
	
	public Mail getMail() {
		return mail;
	}
	
	public Seguranca getSeguranca() {
		return seguranca;
	}

	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}
	
	public static class Mail{
		
		private String host;
		private Integer port;
		private String username;
		private String password;
		
		public String getHost() {
			return host;
		}
		
		public void setHost(String host) {
			this.host = host;
		}
		
		public Integer getPort() {
			return port;
		}
		
		public void setPort(Integer port) {
			this.port = port;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
		
	}
	
	public static class S3{
		
		private String accessKeyId;
		private String secretKeyAcess;
		private String bucket = "aw-result-app-arquivos";
		
		public String getAccessKeyId() {
			return accessKeyId;
		}
		
		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}
		
		public String getSecretKeyAcess() {
			return secretKeyAcess;
		}
		
		public void setSecretKeyAcess(String secretKeyAcess) {
			this.secretKeyAcess = secretKeyAcess;
		}

		public String getBucket() {
			return bucket;
		}

		public void setBucket(String bucket) {
			this.bucket = bucket;
		}
		
		
	}

}

