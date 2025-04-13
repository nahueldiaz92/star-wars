package com.conexa.starwars.exception.handlers;

import com.conexa.starwars.dto.ErrorResponse;
import com.conexa.starwars.exception.ApiException;
import com.conexa.starwars.exception.AuthException;
import com.conexa.starwars.exception.UserRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        logger.error("Error de API controlado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()),
                ex.getStatus()
        );
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationException(UserRegistrationException ex) {
        logger.error("Error de registracion controlado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()),
                ex.getStatus()
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(AuthException ex) {
        logger.error("Error de login controlado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()),
                ex.getStatus()
        );
    }

}
