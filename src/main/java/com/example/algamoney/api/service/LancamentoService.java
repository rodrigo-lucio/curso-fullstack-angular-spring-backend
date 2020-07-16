package com.example.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.mail.Mailer;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.storage.S3;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service 
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	@Autowired
	private S3 s3;
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	public Lancamento buscar(Long id) {
		
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(id);
		
		if(!lancamentoEncontrado.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return lancamentoEncontrado.get();
		
	}
	
	public Lancamento salvar(Lancamento lancamento) {
		
		validaPessoa(lancamento);
		
		if(StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}
		
		return lancamentoRepository.save(lancamento);
		
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		
		Lancamento lancamentoSalvo = buscar(codigo);	
		
		if(lancamentoSalvo.getPessoa().getCodigo() != lancamento.getPessoa().getCodigo()) {
			validaPessoa(lancamento);
		}
		
		// Remove caso seja passado vazio na tag
		if(StringUtils.isEmpty(lancamento.getAnexo()) && StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			s3.remover(lancamentoSalvo.getAnexo());
			
		// Substitui caso tiver algo na tag e o mesmo for diferente do que já existe na base
		}else if(StringUtils.hasText(lancamento.getAnexo()) && !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
			
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");	
		return lancamentoRepository.save(lancamentoSalvo);
		
	}
	
	private void validaPessoa(Lancamento lancamento) {
		
		Pessoa pessoaEncontrada = null;
		
		if(lancamento.getPessoa().getCodigo() != null && lancamento.getPessoa().getCodigo() != 0) {
			pessoaEncontrada = pessoaRepository.findById(lancamento.getPessoa().getCodigo()).get();
		}
				
		if(pessoaEncontrada == null || pessoaEncontrada.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
				
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
		
	}
	
	// Exemplos de agendador de tarefas: 
	//@Scheduled(fixedDelay = 1000 * 5)				//Executa a cada 5 segundos esse metodo                
	//@Scheduled(cron = "0 52 22 * * *")		    //Especifica o horario ou dia que o metodo seja executado, nesse caso as 22:52:00, se quiser especificar os dias, deve-se colocar, CUIDAR O UTC NA CLASSE PRINCIPAL, aqui esta executando errado
	 //segundo minuto hora - os outros sao dias que pode especificar
	//@Scheduled(fixedDelay = 1000 * 60 * 30)       // Executa a cada meia hora - 
	public void avisarSobreLancamentosVencidos() throws InterruptedException {
		
		if(logger.isDebugEnabled()) {
			logger.debug("Preparando envio de e-mails de aviso de lançamentos vencidos.");
		}
		
		
		List<Lancamento> vencidos = 
				lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if(vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso");			
			return;
		}
		
		logger.info("Existem {} lancamentos vencidos.", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);
		
		if(destinatarios.isEmpty()) {
			logger.info("Existem lançamentos vencidos, mas o sistema não encontrou destinatarios.");
			return;
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Envio de e-mail de aviso concluido.");
	}
	
}
