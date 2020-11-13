 package com.example.algamoney.api.config;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")				
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{		

	@Autowired
	private AuthenticationManager authenticationManager;									
	
	@Autowired
	private UserDetailsService userDetailsService;									
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("angular")															
				.secret("$2a$10$G1j5Rf8aEEiGc/AET9BA..xRR.qCpOUzBZoJd8ygbGy6tb3jsMT9G")			
				.scopes("read", "write")														
				.authorizedGrantTypes("password", "refresh_token")								
				.accessTokenValiditySeconds(86400) //60 - 60 * 5										
				.refreshTokenValiditySeconds(3600 * 24) 									
			.and()
				.withClient("mobile")															// Usuario de exemplo apenas para leitura		
				.secret("$2a$10$IMY3GfMpiUjHhJVLNNQgkeB8tsw4W9OmUnQOqw9RDlI/xuBKWKaOi")			
				.scopes("read")																	
				.authorizedGrantTypes("password", "refresh_token")								
				.accessTokenValiditySeconds(1800)
				.refreshTokenValiditySeconds(3600 * 24) 	
		;
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

		endpoints
			.tokenStore(tokenStore())										
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false)				
			.userDetailsService(this.userDetailsService)
			.authenticationManager(this.authenticationManager);
		}
	
	
	 
   /* 
	* Define a assinatura do JWT
    */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {	 				

		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
			
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}
	
}
 