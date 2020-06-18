package com.example.algamoney.api;

import java.time.LocalDate;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AlgamoneyApiApplication {

	private static ApplicationContext APPLICATION_CONTEXT;
	
	public static void main(String[] args) {
		APPLICATION_CONTEXT = SpringApplication.run(AlgamoneyApiApplication.class, args);	
	}

	//Retorna a instancia de qualquer calsse, para utilizar em casos que não consigo utilizar injeção de dependencia
	public static <T> T getBean(Class<T> type) {
		return APPLICATION_CONTEXT.getBean(type);
	}
	
	@PostConstruct
	void started() {
		// Campo data das consultas estava retornando um dia a menos, resolvido com isso
		TimeZone.setDefault(TimeZone.getTimeZone("UTC-3"));
	//	System.out.println(LocalDate.now());
	}

}
