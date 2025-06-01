package com.delivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class DeliveryApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(DeliveryApplication.class, args);
		} catch (Exception e) {
			log.error("Erro ao iniciar a aplicação: ", e);
			System.exit(1);
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("=== DELIVERY APPLICATION STARTED SUCCESSFULLY ===");
		log.info("H2 Console: http://localhost:8080/h2-console");
		log.info("API Base URL: http://localhost:8080/api");
		log.info("===============================================");
	}
}