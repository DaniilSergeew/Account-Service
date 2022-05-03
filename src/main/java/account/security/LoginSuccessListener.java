package account.security;

import account.service.EmplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    EmplService emplService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String email= event.getAuthentication().getName();
        emplService.resetFailedAttempts(email);
    }
}