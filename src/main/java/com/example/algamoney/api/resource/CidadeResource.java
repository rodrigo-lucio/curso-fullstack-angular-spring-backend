package com.example.algamoney.api.resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.exceptionhandler.AlgaMoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Cidade;
import com.example.algamoney.api.model.Estado;
import com.example.algamoney.api.repository.CidadeRepository;
import com.example.algamoney.api.service.exception.RecursoJaCadastradoException;

@RestController
@RequestMapping("/cidades")
public class CidadeResource {

	@Autowired	
	private CidadeService cidadeService;

	@Autowired
	public CidadeRepository cidadeRepository;
	
	@Autowired
	public MessageSource messageSource;
	
	@GetMapping
	public List<Cidade> pesquisar(@RequestParam(required = false) Long estado) {	
		return cidadeRepository.findByEstadoCodigo(estado);
	}

	@GetMapping("/filtrar")
	public List<Cidade> pesquisarPorNome(@RequestParam String nome, @RequestParam String uf){
		return cidadeService.buscarPorNomeEUf(nome, uf);
	}
	
	@PostMapping
	public ResponseEntity<Cidade> inserir(@RequestBody Cidade cidade) {

		Cidade novaCidade = cidadeService.salvar(cidade);
		return ResponseEntity.status(HttpStatus.CREATED).body(novaCidade);
		
	}

	@ExceptionHandler({ RecursoJaCadastradoException.class }) 
	public ResponseEntity<Object> handleRecursoJaCadastradoExceptionException(RecursoJaCadastradoException ex) {

		String mensagemUsuario = messageSource.getMessage("recurso.ja-cadastrada", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro("Cidade " + mensagemUsuario, mensagemDesenvolvedor));

		return ResponseEntity.badRequest().body(erros);

	}

}
