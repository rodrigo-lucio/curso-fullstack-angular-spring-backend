package com.example.algamoney.api.repository.filter;

import javax.persistence.TypedQuery;

import org.springframework.data.domain.Pageable;

public class Paginacao {

	public Paginacao() {
		super();
	}

	protected void adicionarRestricoesPaginacao(TypedQuery<?> query, Pageable pageable) {
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
		
	}

}