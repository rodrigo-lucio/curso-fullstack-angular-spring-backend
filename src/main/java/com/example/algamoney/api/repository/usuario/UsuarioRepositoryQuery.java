
package com.example.algamoney.api.repository.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.projection.ResumoUsuario;

public interface UsuarioRepositoryQuery {

	public Page<Usuario> filtrar(String usuario, Pageable pageable);
	public Page<ResumoUsuario> resumir(String usuario, Pageable pageable);
	
}
