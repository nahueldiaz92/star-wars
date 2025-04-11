package com.conexa.starwars.exceptions;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(int page) {
        super("Page " + page + " not found" );
    }
}
