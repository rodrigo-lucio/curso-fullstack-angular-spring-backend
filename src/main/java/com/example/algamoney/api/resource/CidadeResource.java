package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.model.Cidade;
import com.example.algamoney.api.repository.CidadeRepository;

@RestController
@RequestMapping("/cidades")
public class CidadeResource {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@GetMapping
	public List<Cidade> pesquisar(@RequestParam(required = false) Long estado){
		return cidadeRepository.findByEstadoCodigo(estado);
	}
	
}
