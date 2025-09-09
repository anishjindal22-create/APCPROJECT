package com.example.emailassistant.bootstrap;

import com.example.emailassistant.user.Role;
import com.example.emailassistant.user.RoleRepository;
import com.example.emailassistant.user.User;
import com.example.emailassistant.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder,
                               @Value("${admin.email:admin@example.com}") String adminEmail,
                               @Value("${admin.password:admin12345}") String adminPassword) {
        return args -> {
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPasswordHash(encoder.encode(adminPassword));
                admin.setRoles(Set.of(roleUser, roleAdmin));
                userRepository.save(admin);
            }
        };
    }
}

