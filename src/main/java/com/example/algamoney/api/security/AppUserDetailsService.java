package com.example.algamoney.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Permissao;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;

@Service
public class AppUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;							//Precisamos de uma implementaçao dele, que esta em AppUserDetailsService
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		List<Usuario> usuarios = usuarioRepository.findByEmail(email);
				
		if(usuarios.isEmpty()) {
		   throw new UsernameNotFoundException("Usuário e/ou senha incorretos");
		}
		
		Usuario usuarioEncontrado = usuarios.get(0);
		
		return new User(email, usuarioEncontrado.getSenha(), getPermissoes(usuarioEncontrado));
		
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {					//Adiciona as permissoes da tabela 
	
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		for (Permissao permissao : usuario.getPermissoes()) {
			authorities.add(new SimpleGrantedAuthority(permissao.getDescricao().toUpperCase()));
		}
		
		return authorities;
		
	}

}
