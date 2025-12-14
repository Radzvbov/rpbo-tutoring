package ru.radzvbov.rpbo.tutoring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.radzvbov.rpbo.tutoring.domain.Enrollment;
import ru.radzvbov.rpbo.tutoring.enums.EnrollmentStatus;
import ru.radzvbov.rpbo.tutoring.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // DTO для создания записи
    public record CreateEnrollmentRequest(Long studentId, Long courseId) {}

    // GET /api/enrollments
    @GetMapping
    public List<Enrollment> getAll() {
        return enrollmentService.getAll();
    }

    // GET /api/enrollments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(enrollmentService.getById(id));
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/enrollments/student/{studentId}
    @GetMapping("/student/{studentId}")
    public List<Enrollment> getByStudent(@PathVariable Long studentId) {
        return enrollmentService.getByStudent(studentId);
    }

    // GET /api/enrollments/course/{courseId}
    @GetMapping("/course/{courseId}")
    public List<Enrollment> getByCourse(@PathVariable Long courseId) {
        return enrollmentService.getByCourse(courseId);
    }

    // POST /api/enrollments
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateEnrollmentRequest request) {
        try {
            Enrollment enrollment = enrollmentService.create(request.studentId(), request.courseId());
            return ResponseEntity.ok(enrollment);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // PUT /api/enrollments/{id}/status?status=APPROVED
    @PutMapping("/{id}/status")
    public Enrollment updateStatus(
            @PathVariable Long id,
            @RequestParam("status") EnrollmentStatus status
    ) {
        return enrollmentService.updateStatus(id, status);
    }

    // DELETE /api/enrollments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}