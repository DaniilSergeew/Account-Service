package account.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class NotValidException {

    private int status;
    private String message;
    private String path;

    public NotValidException(HttpStatus httpStatus, String path, String message) {
        status = httpStatus.value();
        this.message = message;
        this.path = path;
    }
}