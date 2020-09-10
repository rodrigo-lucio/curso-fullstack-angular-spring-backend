package com.example.algamoney.api.repository.usuario;

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

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.model.Usuario_;
import com.example.algamoney.api.repository.filter.Paginacao;
import com.example.algamoney.api.repository.projection.ResumoUsuario;

public class UsuarioRepositoryImpl extends Paginacao implements UsuarioRepositoryQuery{

	@PersistenceContext											
	private EntityManager manager;
	
	@Override
	public Page<ResumoUsuario> resumir(String usuario, Boolean ativo, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoUsuario> criteriaQuery = builder.createQuery(ResumoUsuario.class);
		
		Root<Usuario> root = criteriaQuery.from(Usuario.class);
		
		criteriaQuery.select(builder.construct(ResumoUsuario.class, 
				root.get(Usuario_.codigo),
				root.get(Usuario_.nome),
				root.get(Usuario_.email),
				root.get(Usuario_.senha),
				root.get(Usuario_.ativo)));
		
		Predicate[] predicates = criaFiltros(usuario, ativo, builder, root);
		
		criteriaQuery.where(predicates);
		Order orderByNome = builder.asc(root.get(Usuario_.nome));
		criteriaQuery.orderBy(orderByNome);
		
		TypedQuery<ResumoUsuario> query = manager.createQuery(criteriaQuery);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		Long totalUsuarios = totalUsuarios(usuario, ativo, builder);
		
		return new PageImpl<>(query.getResultList(), pageable, totalUsuarios);
		
	}


	private Long totalUsuarios(String usuario, Boolean ativo, CriteriaBuilder builder) {
		CriteriaQuery<Long> criteriaCount = builder.createQuery(Long.class);
		Root<Usuario> rootCount = criteriaCount.from(Usuario.class);
		Predicate[] predicatesCount = criaFiltros(usuario, ativo, builder, rootCount);
		criteriaCount.where(predicatesCount);
		criteriaCount.select(builder.count(rootCount));	
		Long totalUsuarios = manager.createQuery(criteriaCount).getSingleResult();
		return totalUsuarios;
	}

	
	private Predicate[] criaFiltros(String usuario, Boolean ativo, CriteriaBuilder builder, Root<Usuario> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(usuario)) {
						
			Predicate usuarioLike = builder.like(root.get(Usuario_.nome), "%" + usuario +"%");
			Predicate emailLike = builder.like(root.get(Usuario_.email), "%" + usuario +"%");
			Predicate condicao = builder.or(usuarioLike, emailLike);
			predicates.add(condicao);
			
		}
		
		if (ativo != null) {
			Predicate usuariosAtivos = builder.equal(root.get(Usuario_.ATIVO), ativo);
			predicates.add(usuariosAtivos);
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
		
	}


	@Override
	public Page<Usuario> filtrar(String usuario, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteriaQuery = builder.createQuery(Usuario.class);
		
		Root<Usuario> root = criteriaQuery.from(Usuario.class);
		
		Predicate[] predicates = criaFiltros(usuario, null, builder, root);
		
		criteriaQuery.where(predicates);
		
		TypedQuery<Usuario> query = manager.createQuery(criteriaQuery);		
		adicionarRestricoesPaginacao(query, pageable);		
		Long totalUsuarios = totalUsuarios(usuario, null, builder);
		
		return new PageImpl<>(query.getResultList(), pageable, totalUsuarios);
	}
	
}
