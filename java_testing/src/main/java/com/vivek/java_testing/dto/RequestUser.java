package com.vivek.java_testing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUser {
    @JsonProperty("user_name")
    @NotNull(message = "user name cannot be null")
    private String userName;
    private String description;
    @NotNull(message = "user id cannot be null")

    private String userId;
    @NotNull(message = "user password cannot be null")
    private String password;
    @NotNull(message = "user key cannot be null")
    private String key;
}
