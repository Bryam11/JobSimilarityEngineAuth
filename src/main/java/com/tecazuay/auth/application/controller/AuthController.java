package com.tecazuay.auth.application.controller;

import com.tecazuay.auth.application.dto.AuthResponseDto;
import com.tecazuay.auth.application.dto.LoginRequestDto;
import com.tecazuay.auth.application.dto.RegisterRequestDto;
import com.tecazuay.auth.domain.model.User;
import com.tecazuay.auth.domain.port.AuthService;
import com.tecazuay.auth.domain.service.JwtTokenProvider;
import com.tecazuay.auth.infrastructure.util.AESUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) throws Exception {
        User user = User.builder()
                .fullName(AESUtil.decrypt(registerRequest.getFullName()))
                .email(AESUtil.decrypt(registerRequest.getEmail()))
                .professionalTitle(AESUtil.decrypt(registerRequest.getProfessionalTitle()))
                .company(AESUtil.decrypt(registerRequest.getCompany()))
                .password(AESUtil.decrypt(registerRequest.getPassword()))
                .build();

        User registeredUser = authService.registerUser(user);

        String token = jwtTokenProvider.generateToken(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDto(token, "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto loginRequest) throws Exception {
        String token = authService.authenticateUser(
                AESUtil.decrypt(loginRequest.getEmail()),
                AESUtil.decrypt(loginRequest.getPassword())
        );

        return ResponseEntity.ok(new AuthResponseDto(token, "Bearer"));
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(jwtTokenProvider.getPublicKeyAsBase64());
    }
}
