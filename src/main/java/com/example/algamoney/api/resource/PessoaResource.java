package com.example.algamoney.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.constantes.roles.PessoaRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.filter.PessoaFilter;
import com.example.algamoney.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
		
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize(PessoaRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<Pessoa> filtrar(PessoaFilter filter, Pageable pageable){
		return pessoaRepository.filtrar(filter, pageable);
	}
	
	@PostMapping
	@PreAuthorize(PessoaRoles.CADASTRAR + " and " + Scopes.WRITE)
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {		
		
		//Substituido pelo de baixo
		//Pessoa pessoaSalva = pessoaRepository.save(pessoa);
		Pessoa pessoaSalva = pessoaService.salvar(pessoa);
		
		//Adicionado por Rodrigo em 03/10/2019 - evitado código repetido para o retornar o header location
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoa.getCodigo()));
		
		//Comentado por Rodrigo em 03/10/2019
		/*Antes era assim: 
		 
		 		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")						 
				.buildAndExpand(codigo).toUri();
				response.setHeader("Location", uri.toASCIIString());
	
				return ResponseEntity.created(uri).body(pessoaSalva);
		 
		 */
		//Trocado para o código abaixo devido a criacao da classe RecursoCriadoEvent
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize(PessoaRoles.PESQUISAR + " and " + Scopes.READ)
	public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {	
		
		Pessoa pessoaEncontrada = pessoaService.buscar(codigo);
		return ResponseEntity.ok(pessoaEncontrada);
		
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize(PessoaRoles.CADASTRAR + " and " + Scopes.WRITE)
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa){
		
		Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);		
		return ResponseEntity.ok(pessoaSalva);
		
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT) 										//NO_CONTENT="Consegui fazer oq vc pediu mas nao tenho nada para retornar"
	@PreAuthorize(PessoaRoles.REMOVER + " and " + Scopes.WRITE)
	public void remover(@PathVariable Long codigo) {		
		
		pessoaRepository.deleteById(codigo);		
		
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize(PessoaRoles.CADASTRAR + " and " + Scopes.WRITE)
	public void atualizarPropAtivo(@PathVariable Long codigo, @Valid @RequestBody Boolean ativo){
		
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
		
	}
	
}
