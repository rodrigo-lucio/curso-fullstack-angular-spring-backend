package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Cidade;
import com.example.algamoney.api.model.Estado;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.CidadeRepository;
import com.example.algamoney.api.repository.EstadoRepository;
import com.example.algamoney.api.service.exception.RecursoJaCadastradoException;

@Service
public class CidadeService {

	@Autowired
	public CidadeRepository cidadeRepository;
	@Autowired
	public EstadoRepository estadoRepository;

	public Cidade salvar(Cidade cidade) {

		List<Estado> estados = estadoRepository.findByUf(cidade.getEstado().getUf());

		if (estados.isEmpty()) {
			throw new EmptyResultDataAccessException(1);
		}

		Estado estado = estados.get(0);

		List<Cidade> cidades = cidadeRepository.findByNomeAndEstadoCodigo(cidade.getNome(), estado.getCodigo());

		if (!cidades.isEmpty()) {
			throw new RecursoJaCadastradoException();
		}
		
		
		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);

	}

	public List<Cidade> buscarPorNomeEUf(String nome, String uf) {

		List<Estado> estados = estadoRepository.findByUf(uf);

		if (estados.isEmpty()) {
			throw new EmptyResultDataAccessException(1);
		}

		return cidadeRepository.findByNomeAndEstadoCodigo(nome, estados.get(0).getCodigo());

	}
}