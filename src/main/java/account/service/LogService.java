package account.service;

import account.model.Action;
import account.model.Event;
import account.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class LogService extends EmplService {
    @Autowired
    LogRepository logRepository;

    public void addLog(Action action,
                       String subject,
                       String object,
                       HttpServletRequest request) {
        Event event = new Event(action, subject, object, request);
        logRepository.save(event);
    }

    public List<Event> getLogs(@AuthenticationPrincipal UserDetails details) {
        login(details);
        return logRepository.findAll();
    }
}