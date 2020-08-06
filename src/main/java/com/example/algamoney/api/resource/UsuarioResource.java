package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.constantes.roles.LancamentoRoles;
import com.example.algamoney.api.constantes.roles.UsuarioRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.repository.projection.ResumoUsuario;
import com.example.algamoney.api.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
		
	@GetMapping
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<Usuario> listar(@RequestParam(required = false) String usuario, Pageable pageable) {	
		return usuarioRepository.filtrar(usuario, pageable);
	}
	
	@GetMapping(params = "resumo")
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<ResumoUsuario> resumir(@RequestParam(required = false) String usuario, Pageable pageable){
		return usuarioRepository.resumir(usuario, pageable);
	}
		
	
	@GetMapping("/{codigo}")
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public ResponseEntity<Usuario> buscarPeloCodigo(@PathVariable Long codigo) {
		
		Usuario usuarioEncontrado = usuarioService.buscarPorCodigo(codigo);
	 	return ResponseEntity.ok(usuarioEncontrado);
		
	}

}
