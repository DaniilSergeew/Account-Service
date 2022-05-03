package account.service;

import account.model.User;
import account.request.NewPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.TreeMap;

@Service
public class AuthService extends EmplService {

    @Autowired
    PasswordEncoder encoder;

    public void signup(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        if (userRepository.count() == 0) {
            user.setRole("ADMINISTRATOR");
            user.addRole("ADMINISTRATOR");
        } else {
            user.setRole("USER");
            user.addRole("USER");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEmail(email.toLowerCase());
        userRepository.save(user);
    }

    public ResponseEntity<Map<String, String>> changePass(UserDetails details, NewPasswordRequest password) {
        User user = login(details);
        if (encoder.matches(password.getPassword(), details.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        user.setPassword(encoder.encode(password.getPassword()));
        userRepository.save(user);

        Map<String, String> response = new TreeMap<>();
        response.put("email", user.getEmail());
        response.put("status", "The password has been updated successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}