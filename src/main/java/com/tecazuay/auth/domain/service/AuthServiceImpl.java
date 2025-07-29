package com.tecazuay.auth.domain.service;

import com.tecazuay.auth.application.exception.InvalidCredentialsException;
import com.tecazuay.auth.application.exception.UserAlreadyExistsException;
import com.tecazuay.auth.domain.model.User;
import com.tecazuay.auth.domain.port.AuthService;
import com.tecazuay.auth.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        return userRepository.save(user);
    }

    @Override
    public String authenticateUser(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        return jwtTokenProvider.generateToken(user);
    }
}
