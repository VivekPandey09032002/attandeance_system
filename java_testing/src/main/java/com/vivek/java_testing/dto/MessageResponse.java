package com.vivek.java_testing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageResponse<T> {
    private HttpStatus httpStatus;
    private String message;
    private T entity;
}
