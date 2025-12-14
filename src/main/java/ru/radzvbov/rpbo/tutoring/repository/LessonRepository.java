package ru.radzvbov.rpbo.tutoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radzvbov.rpbo.tutoring.domain.Lesson;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseId(Long courseId);
}