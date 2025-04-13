package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.auth.LoginRequest;
import com.conexa.starwars.dto.auth.RegisterRequest;
import com.conexa.starwars.exception.AuthException;
import com.conexa.starwars.exception.UserRegistrationException;
import com.conexa.starwars.model.User;
import com.conexa.starwars.repository.RoleRepository;
import com.conexa.starwars.repository.UserRepository;
import com.conexa.starwars.service.AuthService;
import com.conexa.starwars.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public void registerUser(RegisterRequest request) {

        validateEmailFormat(request.getEmail());
        validateEmailAvailability(request.getEmail());
        validatePasswordLength(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));

        userRepository.save(user);
    }

    @Override
    public void login(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            // Set authentication into security context manually
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Create session and attach it to the current request
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        } catch (AuthenticationException e) {
            throw new AuthException("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }
    }

    private void validateEmailFormat(String email) {
        if (!Constants.EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserRegistrationException("El formato del email no es valido", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserRegistrationException("El email ya esta en uso", HttpStatus.CONFLICT);
        }
    }

    private void validatePasswordLength(String password) {
        if (password == null || password.length() < 6) {
            throw new UserRegistrationException("La contraseña debe tener al menos 6 digitos", HttpStatus.BAD_REQUEST);
        }
    }
}

