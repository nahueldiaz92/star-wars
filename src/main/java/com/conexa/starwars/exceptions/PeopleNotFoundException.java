package com.conexa.starwars.exceptions;

public class PeopleNotFoundException extends RuntimeException {
    public PeopleNotFoundException(String message) {
        super(message);
    }
}
