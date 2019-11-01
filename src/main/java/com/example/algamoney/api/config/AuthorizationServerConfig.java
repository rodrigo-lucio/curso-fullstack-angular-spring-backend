 package com.example.algamoney.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{		//Estamos configurando o servidor de autorização

	@Autowired
	private AuthenticationManager authenticationManager;									//esse cara vai gerenciar a autenticação pra gente
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("angular")															//é o usuario e senha do angular, da aplicação client
				.secret("$2a$10$G1j5Rf8aEEiGc/AET9BA..xRR.qCpOUzBZoJd8ygbGy6tb3jsMT9G")			//Senha encodada
				.scopes("read", "write")														//Permissões que eu quero dar para o meu usuario, posso dar varias permissões, essas strings somos nós que definimos, e depois vamos trata-las. Não é a string que define se vai ler ou gravar, e sim o que fazemos com ela depois no tratamento
				.authorizedGrantTypes("password", "refresh_token")								//Diz que queremos um refresh_token tbm
				.accessTokenValiditySeconds(1800)
				.refreshTokenValiditySeconds(3600 * 24) 										//refesh token espira em 1 dia
			.and()
				.withClient("mobile")															//Usuario de exemplo apenas para leitura		
				.secret("$2a$10$IMY3GfMpiUjHhJVLNNQgkeB8tsw4W9OmUnQOqw9RDlI/xuBKWKaOi")			//Senha é m0b1l30
				.scopes("read")																	//APENAS escopo read
				.authorizedGrantTypes("password", "refresh_token")								
				.accessTokenValiditySeconds(1800)
				.refreshTokenValiditySeconds(3600 * 24) 	
		;
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.tokenStore(tokenStore())										//estamos armazenando o token em memória
			.accessTokenConverter(accessTokenConverter())
			.reuseRefreshTokens(false)										//Sempre que pedir o refresh, um ooooutro refresh vai ser disponibilizado
			.userDetailsService(this.userDetailsService)					//Utiliza user details service para pegar usuario e senha
			.authenticationManager(this.authenticationManager);
		}
	
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {					//Definimos a assinatura do JWT

		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
			
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
}
 