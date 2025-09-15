package com.example.kay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KayApplication {

	public static void main(String[] args) {
		SpringApplication.run(KayApplication.class, args);
        System.out.println("WELCOME TO KAYS BOOK STORE");
	}

}
