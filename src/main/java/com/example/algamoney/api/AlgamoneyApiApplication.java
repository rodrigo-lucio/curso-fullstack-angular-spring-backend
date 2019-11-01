package com.example.algamoney.api;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlgamoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApiApplication.class, args);
	}

	@PostConstruct
	void started() {
		// Campo data das consultas estava retornando um dia a menos, resolvido com isso
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
