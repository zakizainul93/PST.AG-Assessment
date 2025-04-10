package ag.pst.assessment;

import ag.pst.assessment.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssessmentApplication implements CommandLineRunner {

	@Autowired
	CarService carService;

	public static void main(String[] args) {
		SpringApplication.run(AssessmentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		carService.processUserInput();
	}

}
