package com.conexa.starwars.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.conexa.starwars.dto.auth.LoginRequest;
import com.conexa.starwars.dto.auth.RegisterRequest;
import com.conexa.starwars.exception.AuthException;
import com.conexa.starwars.exception.UserRegistrationException;
import com.conexa.starwars.model.Role;
import com.conexa.starwars.model.User;
import com.conexa.starwars.repository.RoleRepository;
import com.conexa.starwars.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser_WithValidData_ShouldSaveUser() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        authService.registerUser(validRegisterRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email"); // Email inválido
        request.setPassword("password123");

        UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                () -> authService.registerUser(request));

        assertEquals("El formato del email no es valido", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);

        UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                () -> authService.registerUser(validRegisterRequest));

        assertEquals("El email ya esta en uso", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void registerUser_WithShortPassword_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("123");

        UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                () -> authService.registerUser(request));

        assertEquals("La contraseña debe tener al menos 6 digitos", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void registerUser_WhenRoleNotFound_ShouldThrowException() {
        // Arrange
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerUser(validRegisterRequest));

        assertEquals("Rol no encontrado", exception.getMessage());
    }

    @Test
    void login_WithValidCredentials_ShouldAuthenticate() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(request.getSession(true)).thenReturn(session);

        authService.login(validLoginRequest, request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(authentication);
        verify(session).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowAuthException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        AuthException exception = assertThrows(AuthException.class,
                () -> authService.login(validLoginRequest, request));

        assertEquals("Credenciales invalidas", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}
