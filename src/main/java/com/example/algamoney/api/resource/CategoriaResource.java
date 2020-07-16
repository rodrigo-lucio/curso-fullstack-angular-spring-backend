package com.example.algamoney.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.constantes.roles.CategoriaRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
		
	 
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize(CategoriaRoles.PESQUISAR + " and " + Scopes.READ)					
	public List<Categoria> listar(){
		return categoriaRepository.findAll();
	}
	
	@PostMapping
	@PreAuthorize(CategoriaRoles.CADASTRAR + " and " + Scopes.WRITE)
	// @ResponseStatus(HttpStatus.CREATED) não preciso mais disso pois implementei no return
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {		// @Valid - Verifica se o model é valido
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
				
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoria.getCodigo()));
		
									
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);                     			// Retorna o JSON da categoria para o client
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize(CategoriaRoles.PESQUISAR + " and " + Scopes.READ)
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {							// @PathVariable, pega o parametro passado na URI
		
		Optional<Categoria> categoriaEncontrada= categoriaRepository.findById(codigo);
		
		if(!categoriaEncontrada.isPresent()) {
			throw new EmptyResultDataAccessException(1);		
		}
		
		return ResponseEntity.ok(categoriaEncontrada.get());
	}
	
}
