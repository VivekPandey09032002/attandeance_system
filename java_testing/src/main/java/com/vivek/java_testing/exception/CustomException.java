package com.vivek.java_testing.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@ToString
@Setter
public class CustomException extends RuntimeException {
    private final List<String> errors;
    private final HttpStatus status;
    public CustomException(String message,List<String> errors,HttpStatus status) {
        super(message);
        this.errors = errors;
        this.status = status;
    }

}
