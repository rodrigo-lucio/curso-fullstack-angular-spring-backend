package com.example.algamoney.api.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.constantes.roles.LancamentoRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.exceptionhandler.AlgaMoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;
import com.example.algamoney.api.service.LancamentoService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private ApplicationEventPublisher publisher; 
	
	@Autowired
	private MessageSource messageSource;												  //Para ler o arquivo message.properties 	
	
	@GetMapping
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable){   //Pageable recebe os parametros size= e page=
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	@GetMapping(params = "resumo")																	//Chama se tiver um parametro ?resumo na requisição
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable){   //Pageable recebe os parametros size= e page=
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}
	
	
	@GetMapping("/{codigo}")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo){
		
		Lancamento lancamentoEncontrado = lancamentoService.buscar(codigo);
		return ResponseEntity.ok(lancamentoEncontrado);

	}
	
	@PostMapping
	@PreAuthorize(LancamentoRoles.CADASTRAR + " and " + Scopes.WRITE)
	public ResponseEntity<Lancamento> inserir(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);		 
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);			
	}
	
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})			//Faz da mesma forma que um try catch, escuta oq estourar com essa excpetion nesta classe
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex){
			
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString() ;
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		
		return ResponseEntity.badRequest().body(erros);
		
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize(LancamentoRoles.REMOVER + " and " + Scopes.WRITE)
	public void deletar(@PathVariable Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize(LancamentoRoles.CADASTRAR + " and " + Scopes.WRITE)
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento){
		Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);
		return ResponseEntity.ok(lancamentoSalvo);
	}
}
