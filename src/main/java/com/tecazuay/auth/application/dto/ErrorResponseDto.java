package com.tecazuay.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private String path;
    private List<String> details;
}
