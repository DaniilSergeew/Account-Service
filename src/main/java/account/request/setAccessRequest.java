package account.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class setAccessRequest {
    @NotNull
    private String user;
    @Pattern(regexp = "LOCK|UNLOCK", message = "Only LOCK or UNLOCK")
    private String operation;

    public String getUser() {
        return user.toLowerCase();
    }
}