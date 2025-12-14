package ru.radzvbov.rpbo.tutoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radzvbov.rpbo.tutoring.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}