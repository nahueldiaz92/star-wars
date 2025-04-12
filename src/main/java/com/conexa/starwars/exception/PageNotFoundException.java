package com.conexa.starwars.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(int page) {
        super("Pagina " + page + " no encontrada");
    }
}
