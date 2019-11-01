package com.example.algamoney.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;

/*
 * Rodrigo 03/10/2019
 * Evento que dispara o header location ao realizar algum post
 * Antes era feito em toda classe o conteudo do metodo adicionarHeaderLocation
 * Evitando assim repetir o código
 */

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent>{

	@Override
	public void onApplicationEvent(RecursoCriadoEvent recursoCriadoEvent) {
		HttpServletResponse response = recursoCriadoEvent.getResponse();
		Long codigo = recursoCriadoEvent.getCodigo();
		
		adicionarHeaderLocation(response, codigo);
		
	}

	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")						 
				.buildAndExpand(codigo).toUri();
		response.setHeader("Location", uri.toASCIIString());

	}
	

}
