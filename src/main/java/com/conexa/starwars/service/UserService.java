package com.conexa.starwars.service;

import com.conexa.starwars.dto.RegisterRequest;
import com.conexa.starwars.exceptions.UserRegistrationException;
import com.conexa.starwars.model.User;
import com.conexa.starwars.repository.RoleRepository;
import com.conexa.starwars.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserRegistrationException("El email ya esta en uso");
            } else {
                User user = new User();
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(roleRepository.findById(2L)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado")));

                userRepository.save(user);
            }
    }
}
