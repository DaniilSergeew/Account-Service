package account.service;

import account.model.Payment;
import account.model.User;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EmplService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    public static final int MAX_FAILED_ATTEMPTS = 4;

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
    }

    public void resetFailedAttempts(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void lock(User user) {
        if (!user.getRole().equals("ROLE_ADMINISTRATOR")) {
            user.setAccountNonLocked(false);
        }
        userRepository.save(user);
    }

    public User login(UserDetails details) {
        if (details != null) {
            String email = details.getUsername();
            User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
            if (user.getPassword().equals(details.getPassword())) {
                return user;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password!");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must login!");
    }

    public ResponseEntity<?> getPayments(UserDetails details) {
        login(details);
        List<Payment> paymentList = paymentRepository.findByEmployeeIgnoreCaseOrderByPeriodDesc(details.getUsername());
        List<Map<String, String>> response = new ArrayList<>();
        for (Payment payment : paymentList) {
            response.add(toResponse(payment));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getPayment(UserDetails details, String period) {
        login(details);
        String employee = details.getUsername();
        return new ResponseEntity<>(toResponse(paymentRepository.findByEmployeeIgnoreCaseAndPeriod(employee, period)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong period"))), HttpStatus.OK);

    }

    public Map<String, String> toResponse(Payment payment) {
        Map<String, String> response = new TreeMap<>();
        response.put("name", userRepository.findByEmailIgnoreCase(payment.getEmployee()).get().getName());
        response.put("lastname", userRepository.findByEmailIgnoreCase(payment.getEmployee()).get().getLastname());
        response.put("period", payment.getResponsePeriod());
        response.put("salary", payment.getResponseSalary());
        return response;
    }

}