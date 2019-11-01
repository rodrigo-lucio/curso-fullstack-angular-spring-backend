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
@Order(Ordered.HIGHEST_PRECEDENCE)											//prioridade alta
public class RefreshTokenPreProcessor implements Filter{
	/*
	 * Esta classe faz com que o refresh token seja passado automaticamente de dentro do cookie, e e não precise mais ser passado no body da requisição
	 * Ela manipula a requisição e joga para o parametro refresh_token o que esta gravado no cookie
	 */
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)	//Filtro de TODAS as requisições, todas passam por aqui
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;						//Pega a requisição
		
		if(req.getRequestURI().equalsIgnoreCase("/oauth/token") &&					//Verifica se a URI é do oauth
				req.getParameter("grant_type").equals("refresh_token") &&			//AND se estamos passando um grant_type para refresh_token, 
				req.getCookies() != null) {											//AND se tiver algum cookie
			
			for (Cookie cookie : req.getCookies()) {								//Procura o cookie com o nome que demos la no RefreshTokenPostProcessor
				if(cookie.getName().equals("refreshToken")) {
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken);	 			//Sobreescreve o parametro com o valor do refresh_token que esta no cookie
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
		//Seta nos parametros o refreshToken do cookie
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			map.put("refresh_token", new String[] {refreshToken});
			map.setLocked(true);
			return map;
			
		}
		
	}
	
}
