package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken>{
/*
 * Esta classe controla a resposta da uri localhost:8080/oauth/token
 * Vamos interceptar ela para armazenar o refresh token em um cookie para ficar ainda mais seguro, e não mostraremos o mesmo na resposta da requisição
 * Esse código o normandes achou na documentação do spring
 */
	
	@Autowired
	private AlgamoneyApiProperty algamoneyProperty;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

		return returnType.getMethod().getName().equals("postAccessToken");
		
		//Só vai executar o método abaixo quando supports retornar true, ou seja, quando for um método postAcessToken
	}

	@Override
	/*
	 * Será executado antes de escrever o body com os tokens
	 * Criamos um cookie com o refresh token e o adicionamos na resposta
	 * Removemos o refresh_token da resposta
	 */
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;			//convertemos para defaultOauth2 pq nele existe o setRefreshToken
		
		String refreshToken = body.getRefreshToken().getValue();					//Pegamos o valor do refresh token
		
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		
		removerRefreshTokenDoBody(token);
		
		return body;
	}

	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		
		refreshTokenCookie.setHttpOnly(true);													//Apenas em http, ou seja, em javascript nao vai ser acessado
		refreshTokenCookie.setSecure(algamoneyProperty.getSeguranca().isEnableHttps()); 
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");						//para qual caminho esse cookie deve ser enviado
		refreshTokenCookie.setMaxAge(2592000);													//Esse cookie espira em dois dias
		
		resp.addCookie(refreshTokenCookie);
		
	}

	
	

}
