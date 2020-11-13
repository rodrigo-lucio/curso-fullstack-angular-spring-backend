package com.example.algamoney.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Mensagem;
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

		if (usuario.getSenha() == null) {
			usuario.setSenha("123mudar");
		}

		String novaSenha = criptografaSenha(usuario.getSenha());
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
			String novaSenha = criptografaSenha(usuario.getSenha());
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

	private String criptografaSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}

	private boolean comparaSenha(String valor, String senhaCodificada) {
		return new BCryptPasswordEncoder().matches(valor, senhaCodificada);
	}

	public ResponseEntity<Mensagem> verificaSenhas(Long codigo, String senhaAtual, String novaSenha,
			String novaSenhaConf) {

		Usuario usuarioEncontrado = buscarPorCodigo(codigo);

		Mensagem mensagem;
		if (!comparaSenha(senhaAtual, usuarioEncontrado.getSenha())) {

			mensagem = new Mensagem("Senha atual invalida", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem);

		} else if (!novaSenha.equals(novaSenhaConf)) {

			mensagem = new Mensagem("Novas senhas nao conferem", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem);

		} else if (comparaSenha(novaSenha, usuarioEncontrado.getSenha())) {

			mensagem = new Mensagem("Nova senha deve ser diferente da anterior", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem);

		}

		usuarioEncontrado.setSenha(criptografaSenha(novaSenha));
		Usuario usuarioAtualizado = atualizar(codigo, usuarioEncontrado);

		// Para não mostrar todas as permissoes
		usuarioAtualizado.setPermissoes(null);
		mensagem = new Mensagem("Senha alterada com sucesso", usuarioAtualizado);

		return ResponseEntity.ok(mensagem);

	}

}
