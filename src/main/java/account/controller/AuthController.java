package account.controller;

import account.model.Action;
import account.model.User;
import account.request.NewPasswordRequest;
import account.service.AuthService;
import account.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    LogService logService;

    @PostMapping("/signup")
    public User signup(@Valid @RequestBody User user,
                       HttpServletRequest httpServletRequest) {
        authService.signup(user);
        logService.addLog(Action.CREATE_USER, "Anonymous", user.getEmail(), httpServletRequest);
        return user;
    }

    @PostMapping("/changepass")
    public ResponseEntity<?> changePass(@Valid @AuthenticationPrincipal UserDetails details,
                                        @Valid @RequestBody NewPasswordRequest newPasswordRequest,
                                        HttpServletRequest httpServletRequest) {
        ResponseEntity<Map<String, String>> response = authService.changePass(details, newPasswordRequest);
        String email = details.getUsername();
        logService.addLog(Action.CHANGE_PASSWORD, email, email, httpServletRequest);
        return response;
    }
}