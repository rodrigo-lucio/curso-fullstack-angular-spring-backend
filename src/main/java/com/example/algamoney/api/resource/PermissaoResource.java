package com.example.algamoney.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.constantes.roles.UsuarioRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.model.Permissao;

import com.example.algamoney.api.repository.usuario.PermissaoRepository;

@RestController
@RequestMapping("/permissoes")
public class PermissaoResource {

	@Autowired
	public PermissaoRepository permissaoRepository;
	
	@GetMapping
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public List<Permissao> listar(){
		return permissaoRepository.findAll(); 
	}
	
	@GetMapping(params = "tipoPermissao")
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public List<Permissao> listarPortipo(@RequestParam Long tipoPermissao){
		return permissaoRepository.findByTipoPermissao(tipoPermissao);
	}
	
	
	@GetMapping("/{codigo}")
	@PreAuthorize(UsuarioRoles.PESQUISAR + " and " + Scopes.READ)
	public Permissao buscarPermissao(@PathVariable Long codigo) {
		
		Optional<Permissao> permissao = permissaoRepository.findById(codigo);
		
		if(!permissao.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return permissao.get();
	}
}
