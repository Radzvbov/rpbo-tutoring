package ru.radzvbov.rpbo.tutoring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.radzvbov.rpbo.tutoring.domain.User;
import ru.radzvbov.rpbo.tutoring.domain.UserSession;
import ru.radzvbov.rpbo.tutoring.dto.AuthResponse;
import ru.radzvbov.rpbo.tutoring.dto.LoginRequest;
import ru.radzvbov.rpbo.tutoring.enums.SessionStatus;
import ru.radzvbov.rpbo.tutoring.repository.UserRepository;
import ru.radzvbov.rpbo.tutoring.repository.UserSessionRepository;
import ru.radzvbov.rpbo.tutoring.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public AuthService(UserRepository userRepository,
                       UserSessionRepository userSessionRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // --------- LOGIN ---------

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String sessionId = UUID.randomUUID().toString();

        String accessToken = jwtTokenProvider.generateAccessToken(user, sessionId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user, sessionId);

        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(refreshExpirationMs);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionId(sessionId);
        session.setRefreshToken(refreshToken);
        session.setStatus(SessionStatus.ACTIVE);
        session.setCreatedAt(now);
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(now);
        session.setIpAddress(httpRequest.getRemoteAddr());
        session.setUserAgent(httpRequest.getHeader("User-Agent"));

        userSessionRepository.save(session);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    // --------- REFRESH ---------

    public AuthResponse refresh(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken) ||
                !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        UserSession oldSession = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Refresh token not found"
                ));

        if (oldSession.getStatus() != SessionStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Session is not active"
            );
        }

        Instant now = Instant.now();
        if (oldSession.getExpiresAt().isBefore(now)) {
            oldSession.setStatus(SessionStatus.EXPIRED);
            userSessionRepository.save(oldSession);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expired"
            );
        }

        // помечаем старую сессию как REVOKED (одноразовый refresh)
        oldSession.setStatus(SessionStatus.REVOKED);
        oldSession.setLastUsedAt(now);
        userSessionRepository.save(oldSession);

        User user = oldSession.getUser();

        String newSessionId = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user, newSessionId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user, newSessionId);

        Instant newExpiresAt = now.plusMillis(refreshExpirationMs);

        UserSession newSession = new UserSession();
        newSession.setUser(user);
        newSession.setSessionId(newSessionId);
        newSession.setRefreshToken(newRefreshToken);
        newSession.setStatus(SessionStatus.ACTIVE);
        newSession.setCreatedAt(now);
        newSession.setExpiresAt(newExpiresAt);
        newSession.setLastUsedAt(now);
        // можно перенести ip/user-agent старой сессии
        newSession.setIpAddress(oldSession.getIpAddress());
        newSession.setUserAgent(oldSession.getUserAgent());

        userSessionRepository.save(newSession);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}