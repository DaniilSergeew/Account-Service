package account.controller;

import account.model.Payment;
import account.service.AcctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/acct")
public class AcctController {

    @Autowired
    AcctService acctService;

    @PostMapping("/payments")
    public ResponseEntity<?> uploadPayments(@Valid @AuthenticationPrincipal UserDetails details,
                                            @Valid @RequestBody List<Payment> paymentList) {
        acctService.uploadPayments(details, paymentList);
        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    @PutMapping("/payments")
    public ResponseEntity<?> updatePayment(@Valid @AuthenticationPrincipal UserDetails details,
                                           @Valid @RequestBody Payment payment) {
        acctService.updatePayment(details, payment);
        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

}