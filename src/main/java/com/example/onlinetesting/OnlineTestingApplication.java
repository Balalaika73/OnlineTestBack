package com.example.onlinetesting;

import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Role;
import com.example.onlinetesting.repository.PersonRepository;
import com.example.onlinetesting.service.Excel.ExcelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@RestController
@EnableCaching
public class OnlineTestingApplication extends SpringBootServletInitializer implements CommandLineRunner {
	private final PersonRepository personRepository;
	private final ExcelService excelService;

	public OnlineTestingApplication(PersonRepository personRepository, ExcelService excelService) {
		this.personRepository = personRepository;
		this.excelService = excelService;
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to OnlineTesting";
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(OnlineTestingApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(OnlineTestingApplication.class);
	}

	@Override
	public void run(String... args) throws IOException {
		List<Person> adminAccount = personRepository.findByRole(Role.ADMIN);
		System.out.println(adminAccount.size());
		if (adminAccount == null || adminAccount.size() == 0) {
			Person person = new Person();
			person.setEmail("admin@gmail.com");
			person.setRole(Role.ADMIN);
			person.setPassword(new BCryptPasswordEncoder().encode("admin"));
			personRepository.save(person);
			System.out.println("Добавлен администратор");
		}
	}
}
