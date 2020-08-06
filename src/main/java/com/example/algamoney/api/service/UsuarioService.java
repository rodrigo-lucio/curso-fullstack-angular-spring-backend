package com.example.algamoney.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	public UsuarioRepository usuarioRepository;
	
	public Usuario buscarPorCodigo(Long id) {
		
		Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(id);
		
		if(!usuarioEncontrado.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return usuarioEncontrado.get();
		
	}
	
	

}
