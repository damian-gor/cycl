package com.example.demo.exception;

import lombok.Data;

@Data
public class MissingRequestParameterException extends RuntimeException {
    private String message;

    public MissingRequestParameterException(String name, String type) {
        this.message = "Należy podać parametr: " + name + " o typie: " + type;
    }
}
