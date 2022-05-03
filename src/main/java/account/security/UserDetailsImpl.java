package account.security;

import account.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserDetailsImpl implements UserDetails {
    @NotNull
    @NotEmpty
    private final String username;

    @NotNull
    @NotEmpty
    private final String password;

    private List<GrantedAuthority> authorities;

    private User user;

    public UserDetailsImpl(User user) {
        username = user.getEmail().toLowerCase();
        password = user.getPassword();
        authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        this.user = user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}