package sg.edu.nus.laps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LapsApplication {

	public static void main(String[] args) {
		// Gen test password hash
		// System.out.println("Hash generated at startup: " + new BCryptPasswordEncoder().encode("12345abc!"));
		SpringApplication.run(LapsApplication.class, args);
	}

}
