package com.example.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter{
	
	/*
	 * Classe para fazer o filtro de requisição do CORS,
	 * Configuramos o CORS manualmente através do Filter
	 * e setamos os parametros necessários para que a requisição de outra origem seja autorizada 
	 * se utilizarmos o @CrossOrigin, poderia resolver tudo isso, mas segundo o normandes ele nao esta funcionando muito bem
	 */
	
	@Autowired
	private AlgamoneyApiProperty algamoneyProperty;


	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Max-Age", "3600");
	    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");
	    response.setStatus(HttpServletResponse.SC_OK);
	    chain.doFilter(req, res);
	    
//		response.setHeader("Access-Control-Allow-Origin", algamoneyProperty.getOriginPermitida());					//Esses dois estao fora pq serão sempre enviados, em todas as requisições, para que um post ou get depois funcionem. Ou seja, para que a aplicação continue funcionando após configurar o CORS
//		response.setHeader("Access-Control-Allow-Credentials", "true");												//Para o cookie ser enviado				
//		
//		if(request.getMethod().equalsIgnoreCase("OPTIONS") && 
//				algamoneyProperty.getOriginPermitida().equals(request.getHeader("Origin"))) {		//Verifica se é um metodo options e se a origem for localhost:8000 (outra origem), e "libera" a requisição
//						
//			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
//        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
//        	response.setHeader("Access-Control-Max-Age", "3600");
//			
//			response.setStatus(HttpServletResponse.SC_OK);			
//			
//		}else {
//			//Se não, continua
//			chain.doFilter(req, res);
//		}
		
		
	}

}
