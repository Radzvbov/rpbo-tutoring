package ru.radzvbov.rpbo.tutoring.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.radzvbov.rpbo.tutoring.domain.Enrollment;
import ru.radzvbov.rpbo.tutoring.enums.EnrollmentStatus;
import ru.radzvbov.rpbo.tutoring.repository.CourseRepository;
import ru.radzvbov.rpbo.tutoring.repository.EnrollmentRepository;
import ru.radzvbov.rpbo.tutoring.repository.UserRepository;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<Enrollment> getAll() {
        return enrollmentRepository.findAll();
    }

    public Enrollment getById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found: " + id));
    }

    public List<Enrollment> getByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Enrollment create(Long studentId, Long courseId) {
        // простая валидация: проверяем, что студент и курс существуют
        if (!userRepository.existsById(studentId)) {
            throw new NoSuchElementException("Student not found: " + studentId);
        }
        if (!courseRepository.existsById(courseId)) {
            throw new NoSuchElementException("Course not found: " + courseId);
        }

        // защита от двойной записи
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student already enrolled to this course");
        }

        Enrollment enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.PENDING);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateStatus(Long id, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found: " + id));

        enrollment.setStatus(status);
        return enrollmentRepository.save(enrollment);
    }

    public void delete(Long id) {
        enrollmentRepository.deleteById(id);
    }
}