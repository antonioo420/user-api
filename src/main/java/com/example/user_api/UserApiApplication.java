package com.example.user_api;

import com.example.user_api.model.User;
import com.example.user_api.repository.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;                     
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class UserApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
	}
    
    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // Revisar si ya existe un admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // contraseña inicial
                admin.setCreationDate(LocalDateTime.now());
                userRepository.save(admin);
                System.out.println("Admin creado: admin / admin123");
            }
        };
    }
}
