package com.enigmacamp.enigmaCademy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EnigmaCademyApplication {

	public static void main(String[] args) {
		Dotenv.configure()
				.ignoreIfMissing()
				.systemProperties()
				.load();

		SpringApplication.run(EnigmaCademyApplication.class, args);
	}

}
