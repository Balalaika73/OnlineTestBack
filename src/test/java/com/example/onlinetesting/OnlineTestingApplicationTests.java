package com.example.onlinetesting;


import com.example.onlinetesting.dto.JwtAuthenticationResponse;
import com.example.onlinetesting.dto.SignInRequest;
import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.service.Auth.AuthenticationService;
import com.example.onlinetesting.service.JWT.JWTService;
import org.junit.jupiter.api.Test;
import com.example.onlinetesting.repository.PersonRepository;
import com.example.onlinetesting.repository.TestRepository;
import com.example.onlinetesting.service.Code.CodeServiceImpl;
import org.assertj.core.api.Assertions;

import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.management.remote.JMXAuthenticator;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

//@SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

class OnlineTestingApplicationTests {

	/*@Autowired
	private PersonRepository personRepository;

	@Test
	void SignUp_Success() {
		Person person = Person.builder()
				.email("kiren1187@gmail.com").password("123456").build();

		Person savePerson = personRepository.save(person);

		Assertions.assertThat(savePerson).isNotNull();
		Assertions.assertThat(savePerson.getId()).isGreaterThan(0);
	}

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTService jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void signin_Success() {
		// Arrange
		SignInRequest signInRequest = new SignInRequest();
		signInRequest.setEmail("test@example.com");
		signInRequest.setPassword("password");

		Person person = new Person();
		person.setEmail(signInRequest.getEmail());
		person.setPassword(passwordEncoder.encode(signInRequest.getPassword()));

		when(personRepository.findByEmail(signInRequest.getEmail())).thenReturn(person);

		// Act
		JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signin(signInRequest);

		// Assert
		assertNotNull(jwtAuthenticationResponse);
		assertEquals(person.getEmail(), jwtAuthenticationResponse.getUserId());
		assertEquals(person.getRole().toString(), jwtAuthenticationResponse.getRole());
	}

	@Test
	void signin_UserNotFound() {
		// Arrange
		SignInRequest signInRequest = new SignInRequest();
		signInRequest.setEmail("nonexistent@example.com");
		signInRequest.setPassword("password");

		when(personRepository.findByEmail(signInRequest.getEmail())).thenReturn(null);

		// Act
		JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signin(signInRequest);

		// Assert
		assertNull(jwtAuthenticationResponse);
	}*/

}
