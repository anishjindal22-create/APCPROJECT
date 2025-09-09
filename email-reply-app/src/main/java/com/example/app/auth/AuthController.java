package com.example.app.auth;

import com.example.app.user.User;
import com.example.app.user.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("form", new SignupForm());
		return "signup";
	}

	@PostMapping("/signup")
	public String signupSubmit(@Valid @ModelAttribute("form") SignupForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "signup";
		}
		if (userRepository.existsByUsername(form.getUsername())) {
			result.rejectValue("username", "exists", "Username already exists");
			return "signup";
		}
		User user = User.builder()
				.username(form.getUsername())
				.email(form.getEmail())
				.passwordHash(passwordEncoder.encode(form.getPassword()))
				.roles(Set.of("USER"))
				.build();
		userRepository.save(user);
		return "redirect:/login?registered";
	}

	@Data
	public static class SignupForm {
		@jakarta.validation.constraints.NotBlank
		private String username;
		@jakarta.validation.constraints.Email
		private String email;
		@jakarta.validation.constraints.NotBlank
		private String password;
	}
}