package com.example.algamoney.api.repository.usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

	List<Permissao> findByTipoPermissao(Long tipoPermissao);
	
}