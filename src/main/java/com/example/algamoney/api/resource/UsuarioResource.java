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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.http.HttpResponse;
import com.example.algamoney.api.constantes.roles.LancamentoRoles;
import com.example.algamoney.api.constantes.roles.UsuarioRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.exceptionhandler.AlgaMoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.repository.projection.ResumoUsuario;
import com.example.algamoney.api.service.UsuarioService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.service.exception.RecursoJaCadastradoException;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired 
	public ApplicationEventPublisher publisher;
		
	@Autowired
	public MessageSource messageSource;
	
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
	
	@PostMapping
	@PreAuthorize(UsuarioRoles.CADASTRAR + " and " + Scopes.READ)
	public ResponseEntity<Usuario> inserir(@Valid @RequestBody Usuario usuario, HttpServletResponse httpResponse){
		
		Usuario novoUsuario = usuarioService.salvar(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, httpResponse, usuario.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
		
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize(UsuarioRoles.CADASTRAR + " and " + Scopes.READ)
	public ResponseEntity<Usuario> atualizar(@PathVariable Long codigo, @Valid @RequestBody Usuario usuario){
		
		Usuario usuarioAtualizado = usuarioService.atualizar(codigo, usuario);
		
		return ResponseEntity.ok(usuarioAtualizado);
		
	}
	
	@ExceptionHandler({ RecursoJaCadastradoException.class }) 
	public ResponseEntity<Object> handleRecursoJaCadastradoExceptionException(RecursoJaCadastradoException ex) {

		String mensagemUsuario = messageSource.getMessage("recurso.ja-cadastrado", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro("E-mail " + mensagemUsuario + " para outro usuário", mensagemDesenvolvedor));

		return ResponseEntity.badRequest().body(erros);

	}
}
