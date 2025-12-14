package ru.radzvbov.rpbo.tutoring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radzvbov.rpbo.tutoring.domain.Enrollment;
import ru.radzvbov.rpbo.tutoring.domain.Payment;
import ru.radzvbov.rpbo.tutoring.enums.EnrollmentStatus;
import ru.radzvbov.rpbo.tutoring.enums.PaymentStatus;
import ru.radzvbov.rpbo.tutoring.repository.EnrollmentRepository;
import ru.radzvbov.rpbo.tutoring.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found: " + id));
    }

    public List<Payment> getByEnrollment(Long enrollmentId) {
        return paymentRepository.findByEnrollmentId(enrollmentId);
    }

    public Payment create(Long enrollmentId, BigDecimal amount, String method) {
        // Проверяем, что запись на курс существует
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found: " + enrollmentId));

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("Payment method must be provided");
        }

        Payment payment = new Payment(
                enrollment.getId(),
                amount,
                Instant.now(),
                method,
                PaymentStatus.PENDING
        );

        return paymentRepository.save(payment);
    }

    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found: " + id));

        payment.setStatus(status);
        Payment saved = paymentRepository.save(payment);

        // Бизнес-логика: если платёж оплачен — подтверждаем запись на курс
        if (status == PaymentStatus.PAID) {
            Enrollment enrollment = enrollmentRepository.findById(payment.getEnrollmentId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Enrollment not found for payment: " + payment.getEnrollmentId()));

            enrollment.setStatus(EnrollmentStatus.APPROVED);
            enrollmentRepository.save(enrollment);
        }

        return saved;
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    // ---------- ОТЧЁТЫ ----------

    /**
     * Все платежи по конкретному курсу.
     */
    public List<Payment> getByCourse(Long courseId) {
        // Сначала находим все записи (enrollments) по курсу
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        if (enrollments.isEmpty()) {
            return List.of();
        }

        List<Long> enrollmentIds = enrollments.stream()
                .map(Enrollment::getId)
                .collect(Collectors.toList());

        return paymentRepository.findByEnrollmentIdIn(enrollmentIds);
    }

    /**
     * Общая сумма ОПЛАЧЕННЫХ платежей по конкретному курсу.
     */
    public BigDecimal getTotalAmountByCourse(Long courseId) {
        List<Payment> payments = getByCourse(courseId);

        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Общая сумма ОПЛАЧЕННЫХ платежей по всем курсам.
     */
    public BigDecimal getTotalAmountAllCourses() {
        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}