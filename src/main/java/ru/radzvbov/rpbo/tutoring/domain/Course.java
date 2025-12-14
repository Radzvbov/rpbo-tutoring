package ru.radzvbov.rpbo.tutoring.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String subject;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;

    @Column(name = "created_at", nullable = false, updatable = false,
            insertable = false) // значение берётся из DEFAULT CURRENT_TIMESTAMP в БД
    private Instant createdAt;

    public Course() {
    }

    public Course(String title,
                  String description,
                  String subject,
                  BigDecimal price,
                  Long tutorId) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.price = price;
        this.tutorId = tutorId;
    }

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}