package com.example.algamoney.api.resource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.algamoney.api.constantes.roles.LancamentoRoles;
import com.example.algamoney.api.constantes.scopes.Scopes;
import com.example.algamoney.api.dto.Anexo;
import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.dto.LancamentoEstatisticaMes;
import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.dto.LancamentoEstatisticaValorPessoa;
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.exceptionhandler.AlgaMoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.TipoLancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;
import com.example.algamoney.api.service.LancamentoService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.storage.S3;
import com.lowagie.text.pdf.codec.Base64.OutputStream;

import net.sf.jasperreports.engine.JRException;

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
	private MessageSource messageSource; // Para ler o arquivo message.properties
	
	@Autowired
	private S3 s3;

	@GetMapping
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) { 																		
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}

	@GetMapping(params = "resumo") // Chama se tiver um parametro ?resumo na requisição
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {																					
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}

	@GetMapping("/{codigo}")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {

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

	@ExceptionHandler({ PessoaInexistenteOuInativaException.class }) // Faz da mesma forma que um try catch
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {

		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
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
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento) {
		Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);
		return ResponseEntity.ok(lancamentoSalvo);
	}

	@GetMapping("/estatisticas/por-categoria")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public List<LancamentoEstatisticaCategoria> porCategoria() {
		return this.lancamentoRepository.porCategoria(LocalDate.now());
	}

	@GetMapping("/estatisticas/por-dia")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public List<LancamentoEstatisticaDia> porDia() {
		return this.lancamentoRepository.porDia(LocalDate.now());
	}

	@GetMapping("/estatisticas/por-pessoa")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public List<LancamentoEstatisticaPessoa> porPessoa() {
		return this.lancamentoRepository.porPessoa(
				LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), 
				LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
	}

	@GetMapping("relatorios/por-pessoa")
	public ResponseEntity<byte[]> relatorioPorPessoa(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws JRException {

		byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(relatorio);

	}

	@PostMapping("/anexo")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public Anexo uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {

		// Exemplo para salvar na máquina	
		//	FileOutputStream out = new FileOutputStream("C:\\algaworks\\anexo--" + anexo.getOriginalFilename());
		//	out.write(anexo.getBytes());
		//	out.close();

		String nome = s3.salvarTemporariamente(anexo);
		
		return new Anexo(nome, s3.configurarUrl(nome));

	}
	
	@GetMapping("/estatisticas/valor-por-pessoa")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public List<LancamentoEstatisticaValorPessoa> valorPorPessoa() {
		return this.lancamentoRepository.valorPorPessoa(
				LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), 
				LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), 
				TipoLancamento.DESPESA);
	}
	
	@GetMapping("/estatisticas/por-ano/{ano}")
	@PreAuthorize(LancamentoRoles.PESQUISAR + " and " + Scopes.READ)
	public List<LancamentoEstatisticaMes> porAno(@PathVariable int ano) {
		return this.lancamentoRepository.lancamentosPorano(ano);
	}

}
