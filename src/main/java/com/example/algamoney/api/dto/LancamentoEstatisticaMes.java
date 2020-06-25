package com.example.algamoney.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.algamoney.api.model.TipoLancamento;

/*
 * Classe Modelo para estatisticas de lançamentos por dia
 */
public class LancamentoEstatisticaMes {

	private TipoLancamento tipo;	
	private int mes;	
	private BigDecimal total;
	
	public LancamentoEstatisticaMes(TipoLancamento tipo, int mes, BigDecimal total) {
		super();
		this.tipo = tipo;
		this.mes = mes;
		this.total = total;
	}
	
	public TipoLancamento getTipo() {
		return tipo;
	}
	
	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}
	
	public int getMes() {
		return mes;
	}
	
	public void setMes(int mes) {
		this.mes = mes;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	
	
}
