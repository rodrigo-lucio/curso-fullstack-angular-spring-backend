package com.example.algamoney.api.dto;

import java.math.BigDecimal;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.TipoLancamento;

public class LancamentoEstatisticaValorPessoa {

	
	private Pessoa pessoa;
	
	private BigDecimal total;
	

	public LancamentoEstatisticaValorPessoa(Pessoa pessoa, BigDecimal total) {
		this.pessoa = pessoa;
		this.total = total;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	} 
	
}
