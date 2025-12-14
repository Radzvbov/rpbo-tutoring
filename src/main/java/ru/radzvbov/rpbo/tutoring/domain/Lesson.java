package ru.radzvbov.rpbo.tutoring.domain;

import jakarta.persistence.*;
import ru.radzvbov.rpbo.tutoring.enums.LessonStatus;

import java.time.Instant;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private String topic;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonStatus status;

    public Lesson() {}

    public Lesson(Long id,
                  Long courseId,
                  Long tutorId,
                  String topic,
                  Instant startTime,
                  Integer durationMinutes,
                  LessonStatus status) {
        this.id = id;
        this.courseId = courseId;
        this.tutorId = tutorId;
        this.topic = topic;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public LessonStatus getStatus() { return status; }
    public void setStatus(LessonStatus status) { this.status = status; }
}