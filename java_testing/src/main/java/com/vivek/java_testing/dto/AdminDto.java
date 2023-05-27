package com.vivek.java_testing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {

    @NotNull(message = "user id cannot be null")
    private Integer adminId;
    @NotNull(message = "admin name cannot be null")
    private String adminName;
    @NotNull(message = "user password cannot be null")
    private String password;
}
