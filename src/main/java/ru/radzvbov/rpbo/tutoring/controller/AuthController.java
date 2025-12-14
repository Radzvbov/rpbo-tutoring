package ru.radzvbov.rpbo.tutoring.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.radzvbov.rpbo.tutoring.domain.User;
import ru.radzvbov.rpbo.tutoring.dto.AuthResponse;
import ru.radzvbov.rpbo.tutoring.dto.LoginRequest;
import ru.radzvbov.rpbo.tutoring.dto.RefreshRequest;
import ru.radzvbov.rpbo.tutoring.enums.Role;
import ru.radzvbov.rpbo.tutoring.repository.UserRepository;
import ru.radzvbov.rpbo.tutoring.service.AuthService;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    // ----- DTO для регистрации -----

    public record RegisterRequest(
            String fullName,
            String email,
            String password
    ) {}

    public record RegisterResponse(
            Long id,
            String fullName,
            String email,
            Role role
    ) {}

    // ----- Регистрация (как было) -----

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req.email() == null || req.email().isBlank()) {
            return ResponseEntity.badRequest().body("Email must be provided");
        }

        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("User with this email already exists");
        }

        if (!isPasswordStrong(req.password())) {
            return ResponseEntity.badRequest().body(
                    "Password is too weak. " +
                    "It must be at least 8 characters long and contain " +
                    "uppercase, lowercase, digit and special character."
            );
        }

        User user = new User();
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(Role.STUDENT);
        user.setCreatedAt(Instant.now());
        User saved = userRepository.save(user);

        RegisterResponse resp = new RegisterResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole()
        );

        return ResponseEntity.ok(resp);
    }

    // ----- Логин: выдаём пару access + refresh -----

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request,
                              HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    // ----- Обновление токенов по refresh -----

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    // ----- Проверка надёжности пароля для регистрации -----

    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}