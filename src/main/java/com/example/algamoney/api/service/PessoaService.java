package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Contato;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

/*
 * Regras de negocio de pessoa
 */

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa buscar(Long codigo) {
		
		Optional<Pessoa>  pessoaEncontrada = pessoaRepository.findById(codigo);
		
		if(!pessoaEncontrada.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return pessoaEncontrada.get();
		
		//Tambem poderia ser assim:
	    //  Pessoa pessoaSalva = pessoaRepository.findById(codigo).						//Lança a excpetion caso for null
		//		orElseThrow(() -> new EmptyResultDataAccessException(1));						
		
	}
	
	public Pessoa salvar(Pessoa pessoa) {
		
		//Força a configuracao da pessoa dentro do contato
		for(Contato contato : pessoa.getContatos()) {
			contato.setPessoa(pessoa);
		}
		
		
		return pessoaRepository.save(pessoa);
		
	}
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {				
		Pessoa pessoaSalva = buscar(codigo);		
		
		
		//Para corrigir o problema que no put era passsado contatos[], que era pra ser removido		
		pessoaSalva.getContatos().clear();
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		
		for(Contato contato : pessoaSalva.getContatos()) {
			contato.setPessoa(pessoaSalva);
		}
		
		
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");				// Copia os dados da pessoa do parametro para pessoa Salva, ignorando o "codigo"
		return pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscar(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva); 
	}
	
}
