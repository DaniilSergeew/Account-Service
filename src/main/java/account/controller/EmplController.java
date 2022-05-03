package account.controller;

import account.service.EmplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/empl")
public class EmplController {
    @Autowired
    EmplService emplService;

    @GetMapping("/payment")
    public ResponseEntity<?> getPayment(@Valid @AuthenticationPrincipal UserDetails details,
                                        @RequestParam(required = false) String period) {
        if (period == null) {
            return emplService.getPayments(details);
        }

        return emplService.getPayment(details, period);
    }

}