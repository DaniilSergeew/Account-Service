package account.service;

import account.model.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AcctService extends EmplService {

    public void uploadPayments(UserDetails details, List<Payment> paymentList) {
        login(details);
        for (Payment payment : paymentList) {
            if (paymentRepository.findByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod()).isEmpty()) {
                paymentRepository.save(payment);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment already exists");
            }
        }
    }

    public void updatePayment(UserDetails details, Payment payment) {
        login(details);
        String employee = payment.getEmployee();
        String period = payment.getPeriod();
        if (paymentRepository.findByEmployeeIgnoreCaseAndPeriod(employee, period).isPresent()) {
            paymentRepository.delete(paymentRepository.findByEmployeeIgnoreCaseAndPeriod(employee, period).get());
            paymentRepository.save(payment);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found");
        }
    }

}