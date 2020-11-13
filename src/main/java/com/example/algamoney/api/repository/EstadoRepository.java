package com.example.algamoney.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long>{

	List<Estado> findByUf(String uf);
	
}
