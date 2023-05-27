package com.vivek.java_testing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vivek.java_testing.annotations.ValidDate;
import com.vivek.java_testing.formatter.CustomLocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestBook {
    @JsonProperty("book_id")
    private UUID bookId;
    @JsonProperty("book_name")
    private String bookName;
    @JsonProperty("book_price")
    private Double bookPrice;
    @JsonProperty("author_name")
    private String authorName;
    @ValidDate
    @JsonFormat(pattern="dd-MM-yyyy")
    @JsonProperty("publish_date")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate publishDate;
}
