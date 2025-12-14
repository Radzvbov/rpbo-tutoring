package ru.radzvbov.rpbo.tutoring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radzvbov.rpbo.tutoring.domain.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}