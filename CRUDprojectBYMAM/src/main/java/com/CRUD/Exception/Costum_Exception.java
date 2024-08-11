package com.CRUD.Exception;

import org.springframework.http.HttpStatus;

public class Costum_Exception extends RuntimeException {
    private final HttpStatus httpStatus;

    public Costum_Exception(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}