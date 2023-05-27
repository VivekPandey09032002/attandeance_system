package com.vivek.java_testing.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@Data
public class ErrorBody {
    private String message;
    private HttpStatus status;
    private List<String> errors;
}
