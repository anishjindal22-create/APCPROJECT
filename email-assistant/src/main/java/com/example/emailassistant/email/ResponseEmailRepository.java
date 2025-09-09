package com.example.emailassistant.email;

import com.example.emailassistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResponseEmailRepository extends JpaRepository<ResponseEmail, Long> {
    List<ResponseEmail> findByUserOrderByCreatedAtDesc(User user);
    Optional<ResponseEmail> findByUserAndSubject(User user, String subject);
    void deleteByUserAndSubject(User user, String subject);
}

