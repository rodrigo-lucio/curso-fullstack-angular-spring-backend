package com.example.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.AlgamoneyApiApplication;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.storage.S3;


/*
 * Listener para configurar a url do lancamento,
 * Impossibilitado de utilizar autowired devido a instancia ser do Hibernate
 * Solução: criar um getBean na classe principal da API
 */
public class LancamentoAnexoListener {

	
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if(StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
			
		}
	}
	
}
