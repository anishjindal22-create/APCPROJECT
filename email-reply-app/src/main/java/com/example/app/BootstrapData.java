package com.example.app;

import com.example.app.user.User;
import com.example.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class BootstrapData {
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initUsers(UserRepository users) {
		return args -> {
			if (users.count() == 0) {
				users.save(User.builder()
						.username("admin")
						.email("admin@example.com")
						.passwordHash(passwordEncoder.encode("admin123"))
						.roles(Set.of("ADMIN", "USER"))
						.build());
			}
		};
	}
}