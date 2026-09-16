package com.enigmacamp.enigmaCademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // this is tell the spring to look out/auditing all the annotations that we put in each file, so that it can track the modified object on the persistent context
public class EnigmaCademyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnigmaCademyApplication.class, args);
	}

}
