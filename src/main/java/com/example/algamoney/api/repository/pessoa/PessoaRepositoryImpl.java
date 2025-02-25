package com.example.algamoney.api.repository.pessoa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.Paginacao;
import com.example.algamoney.api.repository.filter.PessoaFilter;

public class PessoaRepositoryImpl extends Paginacao implements PessoaRespositoryQuery {

	@PersistenceContext 
	private EntityManager manager;

	@Override
	public Page<Pessoa> filtrar(PessoaFilter pessoaFilter, Pageable pageable) {

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);

		//cria as restrições
		Predicate[] predicates = criarRetricoes(pessoaFilter, builder, root);		
		criteria.where(predicates);
		
		Order orderByNome = builder.asc(root.get(Pessoa_.nome));
		criteria.orderBy(orderByNome);
		
		
		TypedQuery<Pessoa> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(pessoaFilter)) ;
	}

	private Predicate[] criarRetricoes(PessoaFilter pessoaFilter, CriteriaBuilder builder,
			Root<Pessoa> root) {

		List<Predicate> predicates = new ArrayList<>();

		if(!StringUtils.isEmpty(pessoaFilter.getNome())) {			//where lower(descricao) like '%filtro%'
			predicates.add(builder.like(
					builder.lower(root.get(Pessoa_.nome)), 				//metamodel criado com a biblioteca hibernate-jpamodelgen
					"%" + pessoaFilter.getNome().toLowerCase() + "%")		//OBS adicionei no pom.xml	
					);															
		}
		
		return predicates.toArray(new Predicate[predicates.size()]); 

	}
	
	private Long total(PessoaFilter pessoaFilter) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);
		
		Predicate[] predicates = criarRetricoes(pessoaFilter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));				
		
		return manager.createQuery(criteria).getSingleResult();
	}

}
