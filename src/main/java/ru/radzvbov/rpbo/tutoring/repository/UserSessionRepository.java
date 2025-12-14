package ru.radzvbov.rpbo.tutoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.radzvbov.rpbo.tutoring.domain.User;
import ru.radzvbov.rpbo.tutoring.domain.UserSession;
import ru.radzvbov.rpbo.tutoring.enums.SessionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Поиск сессии по sessionId (который будет лежать в JWT).
     */
    Optional<UserSession> findBySessionId(String sessionId);

    /**
     * Поиск по refresh-токену, чтобы проверить одноразовость.
     */
    Optional<UserSession> findByRefreshToken(String refreshToken);

    /**
     * Все сессии пользователя с заданным статусом.
     * Можно использовать для анализа/отчётов/выключения всех сессий.
     */
    List<UserSession> findByUserAndStatus(User user, SessionStatus status);
}