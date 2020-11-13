package com.example.algamoney.api.model;

/*
 * Classe para retornar alguma mensagem informativa
 */
public class Mensagem {
	
	private String mensagem;
	private Object recurso;
	
	public Mensagem(String mensagem, Object recurso) {
		this.mensagem = mensagem;
		this.recurso = recurso;
	}
	
	public Mensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public Object getRecurso() {
		return recurso;
	}

	public void setRecurso(Object recurso) {
		this.recurso = recurso;
	}
	
}
