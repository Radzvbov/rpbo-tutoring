package ru.radzvbov.rpbo.tutoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radzvbov.rpbo.tutoring.domain.Payment;

import java.util.Collection;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByEnrollmentId(Long enrollmentId);

    // Новый метод для выборки по нескольким записям
    List<Payment> findByEnrollmentIdIn(Collection<Long> enrollmentIds);
}