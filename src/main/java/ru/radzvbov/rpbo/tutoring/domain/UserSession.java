package ru.radzvbov.rpbo.tutoring.domain;

import jakarta.persistence.*;
import ru.radzvbov.rpbo.tutoring.enums.SessionStatus;

import java.time.Instant;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, к которому привязана сессия.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Идентификатор сессии (будем класть в JWT как sessionId).
     * Можно хранить UUID в строке.
     */
    @Column(name = "session_id", nullable = false, unique = true, length = 64)
    private String sessionId;

    /**
     * Статус сессии: ACTIVE / REVOKED / EXPIRED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    /**
     * Токен refresh (или его идентификатор / хэш).
     * Нужен для одноразового использования refresh-токена.
     */
    @Column(name = "refresh_token", nullable = false, unique = true, length = 512)
    private String refreshToken;

    /**
     * Когда сессия/refresh-токен были созданы.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Время, когда refresh-токен (или сессия) истекает.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * Последнее использование refresh токена / любой активности сессии.
     */
    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    /**
     * Дополнительно: IP, с которого выполнен логин.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Дополнительно: user-agent клиента (браузер/приложение).
     */
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    // ---------- Конструкторы ----------

    public UserSession() {
    }

    public UserSession(
            User user,
            String sessionId,
            String refreshToken,
            SessionStatus status,
            Instant createdAt,
            Instant expiresAt,
            String ipAddress,
            String userAgent
    ) {
        this.user = user;
        this.sessionId = sessionId;
        this.refreshToken = refreshToken;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // ---------- Getters / Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    // ---------- Утилита для обновления last_used_at ----------

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (lastUsedAt == null) {
            lastUsedAt = createdAt;
        }
    }
}