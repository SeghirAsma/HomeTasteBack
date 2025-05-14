package HomeTasteGrp.HomeTaste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HomeTasteApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeTasteApplication.class, args);
	}

}
