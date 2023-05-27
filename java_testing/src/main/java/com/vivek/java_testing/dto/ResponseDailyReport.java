package com.vivek.java_testing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDailyReport {
    private String userName;
    private String userId;
    @JsonProperty("in_time")
    private LocalTime inTime;
    @JsonProperty("out_time")
    private LocalTime outTime;
    @JsonProperty("is_late")
    private boolean isLate;
    @JsonProperty("hour_completion")
    private double hourCompletion;
}
