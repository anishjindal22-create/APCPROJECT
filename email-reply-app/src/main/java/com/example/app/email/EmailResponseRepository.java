package com.example.app.email;

import com.example.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailResponseRepository extends JpaRepository<EmailResponse, Long> {
	List<EmailResponse> findByUserOrderByCreatedAtDesc(User user);
	Optional<EmailResponse> findByUserAndSubject(User user, String subject);
	void deleteByUserAndId(User user, Long id);
}