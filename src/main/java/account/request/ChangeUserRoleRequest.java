package account.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRoleRequest {
    @NotNull
    private String user;
    @NotNull
    private String role;
    @Pattern(regexp = "GRANT|REMOVE", message = "Use only GRAND and REMOVE operations!")
    private String operation;
}