package com.example.app.security;

import com.example.app.user.User;
import com.example.app.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/css/**", "/js/**", "/images/**", "/signup", "/h2-console/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login").permitAll()
				.defaultSuccessUrl("/", true)
			)
			.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

		// H2 console frames support
		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return username -> userRepository.findByUsername(username)
			.map(SecurityConfig::asUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	private static UserDetails asUserDetails(User user) {
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPasswordHash())
				.roles(user.getRoles() != null ? user.getRoles().toArray(new String[0]) : new String[]{})
				.build();
	}
}