package com.example.algamoney.api.mail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.LancamentoRepository;

@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	
	@Autowired
	private LancamentoRepository repo;
	
	//@EventListener //Este listener escuta quando a aplicação termina de iniciar
	public void teste(ApplicationReadyEvent event) {
		
		String template = "mail/aviso-lancamentos-vencidos";
		
		List<Lancamento> lancamentos = repo.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		Map<String, Object> variaveis = new HashMap<String, Object>();
		
		variaveis.put("lancamentos", lancamentos);
		
		this.enviarEmail("luciodigo@gmail.com", Arrays.asList("rodrigo_lucio0221@hotmail.com"), "Testando", template, variaveis);
		System.out.println("TERMINADO O ENVIO DE EMAIL");
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		
		try {
			
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Problemas com o envio de email", e);
		}
			
	}
	
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis) {
		
		Context context = new Context(new Locale("pt", "BR"));
	
		variaveis.entrySet().forEach(e -> {
			context.setVariable(e.getKey(), e.getValue());
		});
		//Mesmo que 
		//for ( Map.Entry<String, Object> map  : variaveis.entrySet()) {
		//	context.setVariable(map.getKey(), map.getValue());
		//}
		
		String mensagem = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, destinatarios, assunto, mensagem); 
	}
	
	public void avisarSobreLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> destinatarios) {
		
		Map<String, Object> variaveis = new HashMap<String, Object>();
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = new ArrayList<String>();
		
		//Mais chique
		//List<String> emails2 = destinatarios.stream().map(u -> u.getEmail()).collect(Collectors.toList());
		
		for(Usuario usuario : destinatarios) {
			emails.add(usuario.getEmail());
		}
		
		String template = "mail/aviso-lancamentos-vencidos";
		
		this.enviarEmail("luciodigo@gmail.com", emails, "Lançamentos Vencidos", template, variaveis);
		
	
	}
	
}
