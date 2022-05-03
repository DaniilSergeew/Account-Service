package account.service;

import account.model.Action;
import account.model.User;
import account.request.ChangeUserRoleRequest;
import account.request.setAccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AdminService extends EmplService {
    @Autowired
    LogService logService;

    public User setRoles(UserDetails details, ChangeUserRoleRequest request,
                         HttpServletRequest httpServletRequest) {
        login(details);
        User user = userRepository.findByEmailIgnoreCase(request.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        if (request.getOperation().equals("GRANT")) {
            user.addRole(request.getRole());
        } else if (request.getOperation().equals("REMOVE")) {
            user.removeRole(request.getRole());
        }
        if (request.getOperation().equals("GRANT")) {
            logService.addLog(Action.GRANT_ROLE, details.getUsername().toLowerCase(),
                    "Grant role " + request.getRole() +
                            " to " + request.getUser().toLowerCase(), httpServletRequest);
        } else {
            logService.addLog(Action.REMOVE_ROLE, details.getUsername().toLowerCase(),
                    "Remove role " + request.getRole() +
                            " from " + request.getUser().toLowerCase(), httpServletRequest);
        }
        userRepository.save(user);
        return user;
    }

    public void deleteUser(UserDetails details, String email) {
        login(details);
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(user);

    }

    public List<User> getAllUsersInfo(UserDetails details) {
        login(details);
        return userRepository.findAll();
    }

    public void setAccess(UserDetails details, setAccessRequest setAccessRequest,
                          HttpServletRequest httpServletRequest) {
        login(details);

        User user = userRepository.findByEmailIgnoreCase(setAccessRequest.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        if (user.getRole().equals("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
        user.setAccountNonLocked(!setAccessRequest.getOperation().equals("LOCK"));
        user.setFailedAttempt(0);
        userRepository.save(user);
        Action action = setAccessRequest.getOperation().equals("LOCK") ? Action.LOCK_USER : Action.UNLOCK_USER;
        String object = (setAccessRequest.getOperation().equals("LOCK") ?
                "Lock" : "Unlock") + " user " + setAccessRequest.getUser();
        logService.addLog(action, details.getUsername(), object, httpServletRequest);
    }
}