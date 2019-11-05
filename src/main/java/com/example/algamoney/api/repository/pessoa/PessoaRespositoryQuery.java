package com.example.algamoney.api.repository.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.filter.PessoaFilter;

public interface PessoaRespositoryQuery {
	
	public Page<Pessoa> filtrar(PessoaFilter filter, Pageable pageable);

}
