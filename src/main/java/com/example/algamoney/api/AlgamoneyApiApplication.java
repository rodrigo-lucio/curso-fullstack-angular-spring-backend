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

	// Retorna a instancia de qualquer calsse, para utilizar em casos que não consigo utilizar injeção de dependencia (Config. do S3)
	public static <T> T getBean(Class<T> type) {
		return APPLICATION_CONTEXT.getBean(type);
	}
	
	@PostConstruct
	void started() {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC-3"));

	}

}
