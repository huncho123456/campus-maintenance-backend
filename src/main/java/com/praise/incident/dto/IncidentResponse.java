package com.praise.incident.dto;


import com.praise.incident.enums.Department;
import com.praise.incident.enums.Priority;
import com.praise.incident.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {

    private int statusCode;

    private String message;

    private String incidentNumber;

    private String title;

    private String imageUrl;

    private String description;

    private Priority priority;

    private Status status;

    private String reportedByEmail;

    private String location;

    private Department resolvingDepartment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
