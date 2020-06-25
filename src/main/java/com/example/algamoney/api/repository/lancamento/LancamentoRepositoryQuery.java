	package com.example.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.example.algamoney.api.dto.LancamentoEstatisticaDia;
import com.example.algamoney.api.dto.LancamentoEstatisticaMes;
import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.dto.LancamentoEstatisticaValorPessoa;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.TipoLancamento;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {		//Tem que ser esse nome LancamentoRepository para o spring data jpa conseguir entender 

	
    public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);	
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);
	public List<LancamentoEstatisticaValorPessoa> valorPorPessoa(LocalDate inicio, LocalDate fim, TipoLancamento tipoLancamento);

	
	public Page<Lancamento> filtrar(LancamentoFilter filter, Pageable pageable);	
	public Page<ResumoLancamento> resumir(LancamentoFilter filter, Pageable pageable);
	
	
	
}
