package ru.radzvbov.rpbo.tutoring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.radzvbov.rpbo.tutoring.domain.Payment;
import ru.radzvbov.rpbo.tutoring.enums.PaymentStatus;
import ru.radzvbov.rpbo.tutoring.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ----------- DTO-запросы/ответы -----------

    public record CreatePaymentRequest(
            Long enrollmentId,
            BigDecimal amount,
            String method
    ) {}

    public record CoursePaymentSummaryResponse(
            Long courseId,
            BigDecimal totalPaid
    ) {}

    public record TotalPaymentSummaryResponse(
            BigDecimal totalPaid
    ) {}

    // ----------- Базовые CRUD-эндпоинты -----------

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.getById(id));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public List<Payment> getByEnrollment(@PathVariable Long enrollmentId) {
        return paymentService.getByEnrollment(enrollmentId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreatePaymentRequest req) {
        try {
            Payment payment = paymentService.create(
                    req.enrollmentId(),
                    req.amount(),
                    req.method()
            );
            return ResponseEntity.ok(payment);
        } catch (NoSuchElementException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Payment updateStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status
    ) {
        return paymentService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------- ОТЧЁТНЫЕ ЭНДПОИНТЫ -----------

    /**
     * Все платежи по конкретному курсу.
     */
    @GetMapping("/course/{courseId}")
    public List<Payment> getByCourse(@PathVariable Long courseId) {
        return paymentService.getByCourse(courseId);
    }

    /**
     * Суммарная выручка по конкретному курсу (только PAID).
     */
    @GetMapping("/summary/course/{courseId}")
    public CoursePaymentSummaryResponse getCourseSummary(@PathVariable Long courseId) {
        BigDecimal total = paymentService.getTotalAmountByCourse(courseId);
        return new CoursePaymentSummaryResponse(courseId, total);
    }

    /**
     * Общая выручка по всем курсам (только PAID).
     */
    @GetMapping("/summary/total")
    public TotalPaymentSummaryResponse getTotalSummary() {
        BigDecimal total = paymentService.getTotalAmountAllCourses();
        return new TotalPaymentSummaryResponse(total);
    }
}