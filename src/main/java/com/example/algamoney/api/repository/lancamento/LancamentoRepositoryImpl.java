package com.example.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.dto.LancamentoEstatisticaMes;
import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.dto.LancamentoEstatisticaValorPessoa;
import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.model.TipoLancamento;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.filter.Paginacao;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl extends Paginacao implements LancamentoRepositoryQuery{

	@PersistenceContext											
	private EntityManager manager;
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);		
		Root<Lancamento> root = criteria.from (Lancamento.class);
		
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
		Order orderByVencimento = builder.desc(root.get(Lancamento_.dataVencimento));
		criteria.orderBy(orderByVencimento);
		
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter)) ;
		
	}


	private Predicate[] criarRetricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {			//where lower(descricao) like '%filtro%'
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.descricao)), 				// Metamodel criado com a biblioteca hibernate-jpamodelgen
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%")		
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

	private Long total(LancamentoFilter lancamentoFilter) {
	
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRetricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));		
		
		return manager.createQuery(criteria).getSingleResult();
	}

	@Override
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {

		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaCategoria> criteriaQuery = 
				criteriaBuilder.createQuery(LancamentoEstatisticaCategoria.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		// Mostrando pra criteria como que o LancamentoEstatistica sera construido - no caso SUM por Categoria
		// Deve passar por ordem do construtor depois da classe
		criteriaQuery.select(
				criteriaBuilder.construct(
						LancamentoEstatisticaCategoria.class, 
						root.get(Lancamento_.categoria),
						criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		//Where dataVencimento >= primeiroDia and dataVencimento <= ultimoDia
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia)
				);
		
		criteriaQuery.groupBy(root.get(Lancamento_.categoria));
		
		TypedQuery<LancamentoEstatisticaCategoria> typedQuery = manager.createQuery(criteriaQuery);
					
		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
		
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaDia> criteriaQuery = 
				criteriaBuilder.createQuery(LancamentoEstatisticaDia.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(
				criteriaBuilder.construct(
						LancamentoEstatisticaDia.class, 
						root.get(Lancamento_.tipo),
						root.get(Lancamento_.dataVencimento),
						criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		//Add o where 
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		//Where dataVencimento >= primeiroDia and dataVencimento <= ultimoDia
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia)
				);
		
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));
		
		TypedQuery<LancamentoEstatisticaDia> typedQuery = manager.createQuery(criteriaQuery);
					
		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaPessoa> criteriaQuery = 
				criteriaBuilder.createQuery(LancamentoEstatisticaPessoa.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(
				criteriaBuilder.construct(
						LancamentoEstatisticaPessoa.class, 
						root.get(Lancamento_.tipo),
						root.get(Lancamento_.pessoa),
						criteriaBuilder.sum(root.get(Lancamento_.valor))));
		

		//Where dataVencimento >= primeiroDia and dataVencimento <= ultimoDia
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						inicio),	
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						fim)
				);
		
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));
		
		TypedQuery<LancamentoEstatisticaPessoa> typedQuery = manager.createQuery(criteriaQuery);
					
		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticaValorPessoa> valorPorPessoa(LocalDate inicio, LocalDate fim, TipoLancamento tipoLancamento) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaValorPessoa> criteriaQuery = 
				criteriaBuilder.createQuery(LancamentoEstatisticaValorPessoa.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		Expression sum = criteriaBuilder.sum(root.get(Lancamento_.valor));
		
		criteriaQuery.select(
				criteriaBuilder.construct(
						LancamentoEstatisticaValorPessoa.class, 
						root.get(Lancamento_.pessoa),
						sum));
		
		
		//Where dataVencimento >= primeiroDia and dataVencimento <= ultimoDia
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						inicio),	
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						fim),
				criteriaBuilder.equal(root.get(Lancamento_.TIPO), 
						tipoLancamento)
				
				);
		
		criteriaQuery.groupBy(root.get(Lancamento_.pessoa));
				
		Order orderByValorDesc = criteriaBuilder.desc(sum);
	           
		criteriaQuery.orderBy(orderByValorDesc);

		TypedQuery<LancamentoEstatisticaValorPessoa> typedQuery = manager.createQuery(criteriaQuery);
					
		return typedQuery.setMaxResults(5).getResultList();
	}
	
}
