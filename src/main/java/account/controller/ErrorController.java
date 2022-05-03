package account.controller;

import account.exception.NotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NotValidException> onMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        NotValidException exception = new NotValidException(HttpStatus.BAD_REQUEST,
                request.getDescription(false).substring(4),
                ex.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }
}