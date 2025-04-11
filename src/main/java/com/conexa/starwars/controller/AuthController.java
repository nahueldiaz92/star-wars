package com.conexa.starwars.controller;

import com.conexa.starwars.config.security.UserDetailsServiceImpl;
import com.conexa.starwars.dto.RegisterRequest;
import com.conexa.starwars.exceptions.UserRegistrationException;
import com.conexa.starwars.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping
    public String showAuthChoice() {
        return "auth/auth-choice";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerRequest") RegisterRequest request,
                                  Model model) {
        try {
            userService.registerUser(request);
            return "redirect:/auth/login?registered";
        } catch (UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }

    }
}
