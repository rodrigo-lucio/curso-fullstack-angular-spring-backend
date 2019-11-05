package com.example.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.pessoa.PessoaRespositoryQuery;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRespositoryQuery{

}
