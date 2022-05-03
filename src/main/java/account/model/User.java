package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "Users")
@NoArgsConstructor
public class User {

    private static final List<String> AVAILABLE_ROLES = List.of(
            "ROLE_USER",
            "ROLE_ADMINISTRATOR",
            "ROLE_ACCOUNTANT",
            "ROLE_AUDITOR");

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty(message = "Name is empty")
    private String name;

    @Column
    @NotEmpty(message = "Lastname is empty")
    private String lastname;

    @Column
    @Pattern(regexp = ".+@acme.com", message = "Wrong email")
    @NotEmpty(message = "Email is empty")
    private String email;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    @ElementCollection
    private List<String> roles = new ArrayList<>();

    @Column
    @JsonIgnore
    private String role;

    @JsonIgnore
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @JsonIgnore
    private int failedAttempt;

    public String getRole() {
        return "ROLE_" + role;
    }

    public void addRole(String role) {
        if (roles.contains("ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user already have this role!");
        }
        if (!AVAILABLE_ROLES.contains("ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
        if (role.equals("ACCOUNTANT") && roles.contains("ROLE_ADMINISTRATOR")
                || role.equals("USER") && roles.contains("ROLE_ADMINISTRATOR")
                || role.equals("ADMINISTRATOR") && (roles.contains("ROLE_USER") || roles.contains("ROLE_ADMINISTRATOR") || roles.contains("ROLE_ACCOUNTANT"))
                || role.equals("AUDITOR") && (roles.contains("ROLE_ACCOUNTANT") || roles.contains("ROLE_ADMINISTRATOR"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }
        roles.add("ROLE_" + role);
        Collections.sort(roles);
        this.role = roles.get(0).replaceFirst("ROLE_", "");
    }

    public void removeRole(String role) {
        if (!roles.contains("ROLE_" + role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
        if (role.equals("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        if (roles.size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
        roles.remove("ROLE_" + role);
    }

}