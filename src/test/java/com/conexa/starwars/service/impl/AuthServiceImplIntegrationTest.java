package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.auth.LoginRequest;
import com.conexa.starwars.dto.auth.RegisterRequest;
import com.conexa.starwars.exception.AuthException;
import com.conexa.starwars.exception.UserRegistrationException;
import com.conexa.starwars.model.User;
import com.conexa.starwars.repository.RoleRepository;
import com.conexa.starwars.repository.UserRepository;
import com.conexa.starwars.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;



    @SpringBootTest
    @TestPropertySource(locations = "classpath:application-test.yml") // o .properties
    @Transactional
    class AuthServiceImplIntegrationTest {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private AuthService authService;

        @Autowired
        private PasswordEncoder passwordEncoder;


        @Test
        void registerUser_WithValidData_ShouldCreateUser() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setEmail("newuser@conexa.com");
            request.setPassword("password123");

            // Act
            authService.registerUser(request);

            // Assert
            User createdUser = userRepository.findByEmail("newuser@conexa.com")
                    .orElseThrow(() -> new RuntimeException("User not found"));

            assertNotNull(createdUser);
            assertEquals("newuser@conexa.com", createdUser.getEmail());
            assertTrue(passwordEncoder.matches("password123", createdUser.getPassword()));
            assertEquals("USER", createdUser.getRole().getName());
        }

        @Test
        void registerUser_WithDuplicateEmail_ShouldThrowException() {
            // Arrange
            User existingUser = new User();
            existingUser.setEmail("existinguser@conexa.com");
            existingUser.setPassword(passwordEncoder.encode("password123"));
            existingUser.setRole(roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado")));
            userRepository.save(existingUser);
            userRepository.flush();

            RegisterRequest request = new RegisterRequest();
            request.setEmail("existinguser@conexa.com");
            request.setPassword("password123");

            // Act & Assert
            UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                    () -> authService.registerUser(request));

            assertEquals("El email ya esta en uso", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        }

        @Test
        void registerUser_WithInvalidEmailFormat_ShouldThrowException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setEmail("invalid-email");
            request.setPassword("password123");

            // Act & Assert
            UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                    () -> authService.registerUser(request));

            assertEquals("El formato del email no es valido", exception.getMessage());
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }

        @Test
        void registerUser_WithShortPassword_ShouldThrowException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setEmail("shortpassword@conexa.com");
            request.setPassword("123");

            // Act & Assert
            UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                    () -> authService.registerUser(request));

            assertEquals("La contraseña debe tener al menos 6 digitos", exception.getMessage());
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }

        @Test
        void login_WithValidCredentials_ShouldAuthenticate() {
            // Arrange
            User user = new User();
            user.setEmail("test@conexa.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Rol no encontrado")));
            userRepository.save(user);

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@conexa.com");
            loginRequest.setPassword("password123");

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpSession session = mock(HttpSession.class);
            when(request.getSession(true)).thenReturn(session);

            // Act
            authService.login(loginRequest, request);

            // Assert
            verify(session).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }

        @Test
        void login_WithInvalidCredentials_ShouldThrowAuthException() {
            // Arrange: no creamos ningún usuario con ese email
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("nonexistentuser@conexa.com");
            loginRequest.setPassword("wrongpassword");

            HttpServletRequest request = mock(HttpServletRequest.class);

            // Act & Assert
            AuthException exception = assertThrows(AuthException.class,
                    () -> authService.login(loginRequest, request));

            assertEquals("Credenciales invalidas", exception.getMessage());
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        }
    }

