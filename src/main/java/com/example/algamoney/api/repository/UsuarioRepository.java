package com.example.algamoney.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.projection.ResumoUsuario;
import com.example.algamoney.api.repository.usuario.UsuarioRepositoryQuery;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryQuery{
	
	public List<Usuario> findByEmail(String email);
	
	public List<Usuario> findByNomeOrEmailContaining(String nome, String email);

	public List<Usuario> findByPermissoesDescricao(String premissaoDescricao);
	
}
