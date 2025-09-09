package com.example.app.email;

import com.example.app.user.User;
import com.example.app.user.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmailController {
	private final EmailResponseRepository emailResponseRepository;
	private final UserRepository userRepository;
	private final EmailGeneratorService emailGeneratorService;

	@GetMapping("/")
	public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		List<EmailResponse> emails = emailResponseRepository.findByUserOrderByCreatedAtDesc(user);
		model.addAttribute("emails", emails);
		model.addAttribute("generateForm", new GenerateForm());
		return "index";
	}

	@GetMapping("/view/{id}")
	public String view(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, Model model) {
		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		EmailResponse email = emailResponseRepository.findById(id).orElse(null);
		if (email == null || !email.getUser().getId().equals(user.getId())) {
			return "redirect:/";
		}
		model.addAttribute("email", email);
		return "view";
	}

	@PostMapping("/generate")
	public String generate(@AuthenticationPrincipal UserDetails principal,
						 @ModelAttribute("generateForm") GenerateForm form,
						 BindingResult result, Model model) {
		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		if (form.getIncomingEmail() == null || form.getIncomingEmail().isBlank()) {
			result.rejectValue("incomingEmail", "blank", "Incoming email is required");
		}
		List<EmailResponse> emails = emailResponseRepository.findByUserOrderByCreatedAtDesc(user);
		model.addAttribute("emails", emails);
		if (result.hasErrors()) {
			return "index";
		}
		String reply = emailGeneratorService.generateReply(form.getIncomingEmail());
		model.addAttribute("generatedReply", reply);
		return "index";
	}

	@PostMapping("/save")
	public String save(@AuthenticationPrincipal UserDetails principal,
					 @RequestParam("subject") String subject,
					 @RequestParam("body") String body) {
		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		EmailResponse email = EmailResponse.builder()
				.user(user)
				.subject(subject)
				.body(body)
				.build();
		emailResponseRepository.save(email);
		return "redirect:/";
	}

	@PostMapping("/delete/{id}")
	public String delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		emailResponseRepository.deleteByUserAndId(user, id);
		return "redirect:/";
	}

	@Data
	public static class GenerateForm {
		@NotBlank
		private String incomingEmail;
	}
}