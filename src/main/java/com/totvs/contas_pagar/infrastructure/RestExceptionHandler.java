package com.totvs.contas_pagar.infrastructure;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.totvs.contas_pagar.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<RestErrorMessage> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.CONFLICT,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ContaNotFoundException.class)
    private ResponseEntity<RestErrorMessage> contaNotFoundHandler(ContaNotFoundException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CsvProcessException.class)
    private ResponseEntity<RestErrorMessage> csvProcessHandler(CsvProcessException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<RestErrorMessage> tokenErrorHandler(TokenException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<RestErrorMessage> jwtErrorHandler(JWTVerificationException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String details = String.join(", ", errors);
        RestErrorMessage errorMessage = new RestErrorMessage("Erro de validação", status, details);

        return new ResponseEntity<>(errorMessage, headers, status);
    }
}
