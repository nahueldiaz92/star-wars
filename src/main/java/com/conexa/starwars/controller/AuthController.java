package com.conexa.starwars.controller;

import com.conexa.starwars.annotations.auth.DocumentedLoginResponse;
import com.conexa.starwars.annotations.auth.DocumentedRegisterResponse;
import com.conexa.starwars.dto.auth.LoginRequest;
import com.conexa.starwars.dto.auth.RegisterRequest;
import com.conexa.starwars.exception.UserRegistrationException;
import com.conexa.starwars.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Operaciones de autenticacion")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Realiza el login")
    @DocumentedLoginResponse
    @PostMapping("/api/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        authService.login(loginRequest, request);
        return ResponseEntity.ok().body("Login successful");
    }
    @Operation(summary = "Realiza el registro de un usuario")
    @DocumentedRegisterResponse
    @PostMapping("/api/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        authService.registerUser(request);
        return ResponseEntity.ok().body("User registered successfully");
    }
    @Operation(summary = "Realiza el logout de un usuario")
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok().body("Logout exitoso");
    }

    //Endpoints para las vistas

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
            authService.registerUser(request);
            return "redirect:/auth/login?registered";
        } catch (UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }

    }
}
