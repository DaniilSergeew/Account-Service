package account.repository;

import account.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String employee, String period);
    List<Payment> findByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);
}