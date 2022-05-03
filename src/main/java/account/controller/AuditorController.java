package account.controller;

import account.model.Event;
import account.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/security")
public class AuditorController {

    @Autowired
    LogService logService;

    @GetMapping("/events")
    public List<Event> getEvents(@Valid @AuthenticationPrincipal UserDetails details) {
        return logService.getLogs(details);
    }

}