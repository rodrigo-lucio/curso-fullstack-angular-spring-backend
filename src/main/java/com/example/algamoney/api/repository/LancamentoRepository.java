package com.example.algamoney.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.algamoney.api.dto.LancamentoEstatisticaMes;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{

	public List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate data);
	
	@Query("select new com.example.algamoney.api.dto.LancamentoEstatisticaMes(tipo, month(data_vencimento) as mes, sum(valor) as valor) "
			+ "from Lancamento  where "
			+ "year(data_vencimento) = ?1 "
			+ "group by tipo, month(data_vencimento) "
			+ "order by month(data_vencimento)")
	public List<LancamentoEstatisticaMes> lancamentosPorano(int ano);
	
	
}
