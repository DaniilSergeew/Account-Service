package account.controller;

import account.model.Action;
import account.model.User;
import account.request.ChangeUserRoleRequest;
import account.request.setAccessRequest;
import account.service.AdminService;
import account.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("api/admin/user")
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    LogService logService;

    @PutMapping("/role")
    public User setRoles(@Valid @AuthenticationPrincipal UserDetails details,
                         @Valid @RequestBody ChangeUserRoleRequest changeUserRoleRequest,
                         HttpServletRequest httpServletRequest) {
        return adminService.setRoles(details, changeUserRoleRequest, httpServletRequest);
    }

    @DeleteMapping("/{email}")
    public Map<String, String> deleteUser(@Valid @AuthenticationPrincipal UserDetails details,
                                          @PathVariable String email,
                                          HttpServletRequest httpServletRequest) {
        adminService.deleteUser(details, email);
        Map<String, String> response = new TreeMap<>();
        response.put("user", email);
        response.put("status", "Deleted successfully!");
        logService.addLog(Action.DELETE_USER, details.getUsername(), email, httpServletRequest);
        return response;
    }

    @GetMapping
    public List<User> getAllUsersInfo(@Valid @AuthenticationPrincipal UserDetails details) {
        return adminService.getAllUsersInfo(details);
    }

    @PutMapping("/access")
    public ResponseEntity<?> setAccess(@Valid @AuthenticationPrincipal UserDetails details,
                                       @Valid @RequestBody setAccessRequest setAccessRequest,
                                       HttpServletRequest httpServletRequest) {
        adminService.setAccess(details, setAccessRequest, httpServletRequest);
        String msg = "User " + setAccessRequest.getUser() + " " + (setAccessRequest.getOperation().equals("LOCK") ?
                "locked!" : "unlocked!");
        return new ResponseEntity<>(Map.of("status", msg), HttpStatus.OK);
    }
}