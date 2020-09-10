package com.example.algamoney.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Permissao;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.service.exception.RecursoJaCadastradoException;

@Service
public class UsuarioService {

	@Autowired
	public UsuarioRepository usuarioRepository;

	public Usuario buscarPorCodigo(Long id) {

		Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(id);

		if (!usuarioEncontrado.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}

		return usuarioEncontrado.get();

	}

	public Usuario salvar(Usuario usuario) {

		List<Usuario> usuarios = usuarioRepository.findByEmail(usuario.getEmail());

		if (!usuarios.isEmpty()) {
			throw new RecursoJaCadastradoException();
		}

		if (usuario.getAtivo() == null) {
			usuario.setAtivo(true);
		}

		if(usuario.getSenha() == null) {
			usuario.setSenha("123mudar");
		}
		
		String novaSenha = converteSenha(usuario.getSenha());
		usuario.setSenha(novaSenha);

		return usuarioRepository.save(usuario);
		
	}

	public Usuario atualizar(Long codigo, Usuario usuario) {

		List<Usuario> usuarios = usuarioRepository.findByEmailAndCodigoNot(usuario.getEmail(), codigo);

		if (!usuarios.isEmpty()) {
			throw new RecursoJaCadastradoException();
		}

		Usuario usuarioSalvo = buscarPorCodigo(codigo);

		if (usuario.getNome() == null) {
			usuario.setNome(usuarioSalvo.getNome());
		}
				
		if (usuario.getEmail() == null) {
			usuario.setEmail(usuarioSalvo.getEmail());
		}
		
		if (usuario.getSenha() == null || usuario.getSenha().equals(usuarioSalvo.getSenha())) {
			usuario.setSenha(usuarioSalvo.getSenha());
		} else {
			String novaSenha = converteSenha(usuario.getSenha());
			usuario.setSenha(novaSenha);
		}

		if (usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioSalvo.getAtivo());
		}
		
		if (usuario.getPermissoes() == null) {
			usuario.setPermissoes(usuarioSalvo.getPermissoes());
		}

		BeanUtils.copyProperties(usuario, usuarioSalvo, "codigo");

		return usuarioRepository.save(usuarioSalvo);

	}

	private String converteSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}

}
