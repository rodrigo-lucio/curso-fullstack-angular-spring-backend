package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery{

	@PersistenceContext											//Para poder trabalhar com a consulta
	private EntityManager manager;
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		//cria as restrições
		Predicate[] predicates = criarRetricoes(lancamentoFilter, builder, root);		
		criteria.where(predicates);
		
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter)) ;
	}
	
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(ResumoLancamento.class,
				root.get(Lancamento_.codigo), 
				root.get(Lancamento_.descricao), 
				root.get(Lancamento_.dataVencimento), 
				root.get(Lancamento_.dataPagamento), 
				root.get(Lancamento_.valor), 
				root.get(Lancamento_.tipo), 
				root.get(Lancamento_.categoria).get(Categoria_.nome), 
				root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
	
		Predicate[] predicates = criarRetricoes(lancamentoFilter, builder, root);		
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter)) ;
		
	}


	private Predicate[] criarRetricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {			//where lower(descricao) like '%filtro%'
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.descricao)), 				//metamodel criado com a biblioteca hibernate-jpamodelgen
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%")		//OBS adicionei no pom.xml	
					);															
		}
		
		if(lancamentoFilter.getDataVencimentoDe() != null) {				//and datavencimento >= parametro
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
							lancamentoFilter.getDataVencimentoDe())
					); 
		}
		
		if(lancamentoFilter.getDataVencimentoAte() != null) {				//and datavencimento <= parametro
			predicates.add(
					builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
							lancamentoFilter.getDataVencimentoAte())
					); 
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void adicionarRestricoesPaginacao(TypedQuery<?> query, Pageable pageable) {
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
		
	}
	
	private Long total(LancamentoFilter lancamentoFilter) {
	
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRetricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));				//faz o count(*) pra ver quantos registros tem
		
		return manager.createQuery(criteria).getSingleResult();
	}

	
	
}
