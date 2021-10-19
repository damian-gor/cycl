package com.example.demo.exception;

import lombok.Data;

@Data
public class BadRequestException extends RuntimeException {
    private String message;

    public BadRequestException(String details) {
        this.message = "Błędne zapytanie: " + details;
    }
}
