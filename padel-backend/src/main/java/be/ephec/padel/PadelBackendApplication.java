package be.ephec.padel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PadelBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PadelBackendApplication.class, args);
	}

}
