package ru.radzvbov.rpbo.tutoring.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.radzvbov.rpbo.tutoring.domain.User;
import ru.radzvbov.rpbo.tutoring.enums.Role;
import ru.radzvbov.rpbo.tutoring.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Инициализация тестовых данных
    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash("admin");
            admin.setRole(Role.ADMIN);
            admin.setCreatedAt(Instant.now());
            userRepository.save(admin);

            User student = new User();
            student.setFullName("Student One");
            student.setEmail("student1@example.com");
            student.setPasswordHash("123456");
            student.setRole(Role.STUDENT);
            student.setCreatedAt(Instant.now());
            userRepository.save(student);
        }
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User create(User user) {
        user.setId(null); // пусть БД сама генерирует id
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
        }
        return userRepository.save(user);
    }

    public User update(long id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(updated.getFullName());
                    existing.setEmail(updated.getEmail());
                    existing.setPasswordHash(updated.getPasswordHash());
                    existing.setRole(updated.getRole());
                    return userRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean delete(long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}