package com.praise.incident.dto;


import com.praise.incident.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    // Generic
    private int status;
    private String message;

    // For login
    private String token;
    private UserRole role;
    private Boolean active;
    private String expirationTime;
    private String referredBy;

    // User data
    private String password;
    private UserDto user;
    private List<UserDto> users;

    private final LocalDateTime timestamp = LocalDateTime.now();
}
