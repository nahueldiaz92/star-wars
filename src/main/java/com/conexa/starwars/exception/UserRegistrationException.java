package com.conexa.starwars.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserRegistrationException extends RuntimeException {
    private final HttpStatus status;

    public UserRegistrationException(String message, HttpStatus status)
    {
        super(message);
        this.status = status;
    }
}
