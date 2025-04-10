package com.conexa.starwars.controller;

import com.conexa.starwars.config.security.UserDetailsServiceImpl;
import com.conexa.starwars.dto.RegisterRequestDTO;
import com.conexa.starwars.exceptions.UserRegistrationException;
import com.conexa.starwars.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
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
        model.addAttribute("registerRequest", new RegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerRequest") RegisterRequestDTO request,
                                  Model model) {
        try {
            userService.registerUser(request);
            return "redirect:/auth/login";
        } catch (UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }

    }
}
