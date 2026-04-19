package com.praise.incident.notification;

import com.praise.incident.entity.IncidentEntity;
import com.praise.incident.entity.UserEntity;
import com.praise.incident.enums.Department;
import com.praise.incident.enums.Priority;
import com.praise.incident.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String incidentNumber;
    private String reporterName;
    private String title;
    private String description;
    private Priority priority;
    private String location;
    private String reportedByEmail;
    private Department resolvingDepartment;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationDto fromEntity(IncidentEntity incident) {
        UserEntity user = incident.getUser();
        String reporterName = (user != null)
                ? user.getFirstName() + " " + user.getLastName()
                : "Reporter";

        return NotificationDto.builder()
                .incidentNumber(incident.getIncidentNumber())
                .reporterName(reporterName)
                .title(incident.getTitle())
                .description(incident.getDescription())
                .priority(incident.getPriority())
                .location(incident.getLocation())
                .reportedByEmail(incident.getReportedByEmail())
                .resolvingDepartment(incident.getResolvingDepartment())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }

}