package account.security;

import account.model.Action;
import account.model.User;
import account.repository.UserRepository;
import account.service.EmplService;
import account.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class BadCredentialsListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    @Autowired
    LogService logService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmplService emplService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        logService.addLog(Action.LOGIN_FAILED, email, request.getRequestURI(), request);
        Optional<User> u = userRepository.findByEmailIgnoreCase(email);
        if (u.isPresent()) {
            User user = u.get();
            if (user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < EmplService.MAX_FAILED_ATTEMPTS) {
                    emplService.increaseFailedAttempts(user);
                } else {
                    logService.addLog(Action.BRUTE_FORCE, user.getEmail(),
                            request.getRequestURI(), request);
                    emplService.lock(user);
                    logService.addLog(Action.LOCK_USER, user.getEmail(),
                            "Lock user " + user.getEmail(), request);

                }
            }
        }
    }
}