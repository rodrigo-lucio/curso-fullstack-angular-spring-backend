package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service 
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public Lancamento buscar(Long id) {
		
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(id);
		
		if(lancamentoEncontrado.isEmpty()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return lancamentoEncontrado.get();
		
	}
	
	public Lancamento salvar(Lancamento lancamento) {
		
		validaPessoa(lancamento);
		return lancamentoRepository.save(lancamento);
		
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		
		Lancamento lancamentoSalvo = buscar(codigo);	
		
		if(lancamentoSalvo.getPessoa().getCodigo() != lancamento.getPessoa().getCodigo()) {
			validaPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");	
		return lancamentoRepository.save(lancamentoSalvo);
		
	}
	
	private void validaPessoa(Lancamento lancamento) {
		
		Pessoa pessoaEncontrada = null;
		
		if(lancamento.getPessoa().getCodigo() != null && lancamento.getPessoa().getCodigo() != 0) {
			pessoaEncontrada = pessoaRepository.findById(lancamento.getPessoa().getCodigo()).get();
		}
				
		if(pessoaEncontrada == null || pessoaEncontrada.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
				
	}
	
}
