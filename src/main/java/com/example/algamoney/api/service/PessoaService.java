package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

/*
 * Rodrigo 07/10/2019
 * Regras de negocio de pessoa sao feitas aqui, nao fica legar fazer no resource
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
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {				
		Pessoa pessoaSalva = buscar(codigo);		
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");				//Copia os dados da pessoa do parametro para pessoa Salva, ignorando o "codigo"
		return pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscar(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva); 
	}
	
}
