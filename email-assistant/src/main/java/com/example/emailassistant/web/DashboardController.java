package com.example.emailassistant.web;

import com.example.emailassistant.email.ResponseEmail;
import com.example.emailassistant.email.ResponseEmailRepository;
import com.example.emailassistant.gemini.GeminiService;
import com.example.emailassistant.user.User;
import com.example.emailassistant.user.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@Validated
public class DashboardController {

    private final GeminiService geminiService;
    private final UserRepository userRepository;
    private final ResponseEmailRepository responseEmailRepository;

    public DashboardController(GeminiService geminiService, UserRepository userRepository, ResponseEmailRepository responseEmailRepository) {
        this.geminiService = geminiService;
        this.userRepository = userRepository;
        this.responseEmailRepository = responseEmailRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        List<ResponseEmail> responses = responseEmailRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("responses", responses);
        return "dashboard";
    }

    @PostMapping("/generate")
    public String generate(@AuthenticationPrincipal UserDetails principal,
                           @RequestParam("inputEmail") @NotBlank String inputEmail,
                           Model model) {
        String generated = geminiService.generateResponseEmail(inputEmail);
        model.addAttribute("generated", generated);
        model.addAttribute("inputEmail", inputEmail);
        return "dashboard";
    }

    @PostMapping("/save")
    public String save(@AuthenticationPrincipal UserDetails principal,
                       @RequestParam("subject") @NotBlank String subject,
                       @RequestParam("inputEmail") @NotBlank String inputEmail,
                       @RequestParam("generated") @NotBlank String generated) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        ResponseEmail item = responseEmailRepository.findByUserAndSubject(user, subject)
                .orElseGet(ResponseEmail::new);
        item.setUser(user);
        item.setSubject(subject);
        item.setInputEmail(inputEmail);
        item.setGeneratedResponse(generated);
        responseEmailRepository.save(item);
        return "redirect:/";
    }

    @PostMapping("/delete")
    @Transactional
    public String delete(@AuthenticationPrincipal UserDetails principal,
                         @RequestParam("subject") @NotBlank String subject) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        responseEmailRepository.findByUserAndSubject(user, subject)
                .ifPresent(responseEmailRepository::delete);
        return "redirect:/";
    }

    @GetMapping("/load")
    public String load(@AuthenticationPrincipal UserDetails principal,
                       @RequestParam("subject") String subject,
                       Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        ResponseEmail item = responseEmailRepository.findByUserAndSubject(user, subject).orElse(null);
        List<ResponseEmail> responses = responseEmailRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("responses", responses);
        if (item != null) {
            model.addAttribute("generated", item.getGeneratedResponse());
            model.addAttribute("inputEmail", item.getInputEmail());
        }
        return "dashboard";
    }
}

