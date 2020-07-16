package com.example.algamoney.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)											// Prioridade alta
public class RefreshTokenPreProcessor implements Filter{
	
	/*
	 * Adiciona o refresh token automaticamente de dentro do cookie, evitando de ser passado no body da requisição
	 * Manipula a requisição e joga para o parametro refresh_token o que esta no cookie
	 */
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)	//Filtro de TODAS as requisições, todas passam por aqui
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;						
		
		if(req.getRequestURI().equalsIgnoreCase("/oauth/token") &&					
				req.getParameter("grant_type").equals("refresh_token") &&			 
				req.getCookies() != null) {											
			
			for (Cookie cookie : req.getCookies()) {								
				if(cookie.getName().equals("refreshToken")) {
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken);	 			
				}	
			}
			
		}
		
		chain.doFilter(req, response);
				
	}

	static class MyServletRequestWrapper extends HttpServletRequestWrapper{

		private String refreshToken;
		
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}
		
		@Override
		// Seta nos parametros o refreshToken do cookie
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			map.put("refresh_token", new String[] {refreshToken});
			map.setLocked(true);
			return map;
			
		}
		
	}
	
}
