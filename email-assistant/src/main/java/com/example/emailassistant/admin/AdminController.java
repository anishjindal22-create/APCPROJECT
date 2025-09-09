package com.example.emailassistant.admin;

import com.example.emailassistant.email.ResponseEmailRepository;
import com.example.emailassistant.user.User;
import com.example.emailassistant.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ResponseEmailRepository responseEmailRepository;

    public AdminController(UserRepository userRepository, ResponseEmailRepository responseEmailRepository) {
        this.userRepository = userRepository;
        this.responseEmailRepository = responseEmailRepository;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @PostMapping("/delete-user")
    @Transactional
    public String deleteUser(@RequestParam("id") Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            responseEmailRepository.deleteByUser(user);
            user.getRoles().clear();
            userRepository.save(user);
            userRepository.delete(user);
        }
        return "redirect:/admin/users";
    }
}

