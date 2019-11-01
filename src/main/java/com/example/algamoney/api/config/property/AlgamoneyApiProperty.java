package com.example.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * Classe responsável por configurar o ambiente de produção
 * Chamamos esses atributos nas classes onde precisamos setar true quando estiver rodando em produção
 * Efetuamos as configurações no arquivo de profile applicatio-prod.properties
 */

@Component
@ConfigurationProperties("algamoney")			//habilita essa classe para ser seus atributos serem acessiveis nos arquivos .properties
public class AlgamoneyApiProperty {

	private String originPermitida = "http://localhost:8000";

	private final Seguranca seguranca = new Seguranca();

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

}

