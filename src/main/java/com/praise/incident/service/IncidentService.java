package com.praise.incident.service;

import com.praise.incident.dto.IncidentDto;
import com.praise.incident.dto.IncidentResponse;
import com.praise.incident.entity.IncidentEntity;
import com.praise.incident.entity.UserEntity;
import com.praise.incident.enums.Priority;
import com.praise.incident.enums.Status;
import com.praise.incident.exception.NotFoundException;
import com.praise.incident.repo.IncidentRepo;
import com.praise.incident.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepo incidentRepository;
    private final UserRepo userRepository;
    private final CloudinaryService cloudinaryService;

    public IncidentResponse createIncident(IncidentDto request, MultipartFile file, String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.info("Creating incident for user: {}", email);

        String incidentNumber = String.format("IND-%d-%s",
                incidentRepository.count() + 1,
                UUID.randomUUID().toString().substring(0, 4).toUpperCase());

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(file);
        }

        IncidentEntity incident = IncidentEntity.builder()
                .user(user)
                .incidentNumber(incidentNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .reportedByEmail(user.getEmail())
                .location(request.getLocation())
                .imageUrl(imageUrl)
                .resolvingDepartment(request.getResolvingDepartment())
                .status(Status.OPEN)
                .build();

        IncidentEntity saved = incidentRepository.save(incident);

        return IncidentResponse.builder()
                .statusCode(201)
                .message(saved.getIncidentNumber() + " Incident Created Successfully.")
                .incidentNumber(saved.getIncidentNumber())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .imageUrl(saved.getImageUrl())
                .priority(saved.getPriority())
                .reportedByEmail(saved.getReportedByEmail())
                .location(saved.getLocation())
                .resolvingDepartment(saved.getResolvingDepartment())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    public List<IncidentResponse> getIncidentsByEmail(String email) {

        log.info("Fetching incidents for user: {}", email);

        List<IncidentEntity> incidents = incidentRepository.findByUserEmail(email);

        if (incidents.isEmpty()) {
            throw new NotFoundException("No incidents found for this user");
        }

        return incidents.stream().map(incident -> IncidentResponse.builder()
                .statusCode(200)
                .incidentNumber(incident.getIncidentNumber())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .priority(incident.getPriority())
                .imageUrl(incident.getImageUrl())
                .reportedByEmail(incident.getReportedByEmail())
                .location(incident.getLocation())
                .resolvingDepartment(incident.getResolvingDepartment())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build()
        ).toList();
    }

    public IncidentResponse updateIncident(String incidentNumber, IncidentDto request, MultipartFile file) {
        log.info("Updating incident: {}", incidentNumber);

        IncidentEntity incident = incidentRepository.findByIncidentNumber(incidentNumber)
                .orElseThrow(() -> new NotFoundException("Incident not found with number: " + incidentNumber));

        // Update basic fields if they are provided in the request
        if (request.getTitle() != null) incident.setTitle(request.getTitle());
        if (request.getDescription() != null) incident.setDescription(request.getDescription());
        if (request.getPriority() != null) incident.setPriority(request.getPriority());
        if (request.getLocation() != null) incident.setLocation(request.getLocation());
        if (request.getResolvingDepartment() != null) incident.setResolvingDepartment(request.getResolvingDepartment());
        if (request.getStatus() != null) incident.setStatus(request.getStatus());

        // Handle new image upload
        if (file != null && !file.isEmpty()) {
            String newImageUrl = cloudinaryService.uploadFile(file);
            incident.setImageUrl(newImageUrl);
        }

        IncidentEntity updated = incidentRepository.save(incident);
        return mapToResponse(updated, 200, "Incident updated successfully");
    }

    public void deleteIncident(String incidentNumber) {
        log.info("Deleting incident: {}", incidentNumber);
        IncidentEntity incident = incidentRepository.findByIncidentNumber(incidentNumber)
                .orElseThrow(() -> new NotFoundException("Incident not found"));

        incidentRepository.delete(incident);
    }

    // Helper method to keep code DRY (Don't Repeat Yourself)
    private IncidentResponse mapToResponse(IncidentEntity entity, int statusCode, String message) {
        return IncidentResponse.builder()
                .statusCode(statusCode)
                .message(message)
                .incidentNumber(entity.getIncidentNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .priority(entity.getPriority())
                .reportedByEmail(entity.getReportedByEmail())
                .location(entity.getLocation())
                .resolvingDepartment(entity.getResolvingDepartment())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


    public List<IncidentResponse> getAllIncidents() {
        log.info("Fetching all incidents in the system");

        // Using the standard JPA findAll() method
        List<IncidentEntity> incidents = (List<IncidentEntity>) incidentRepository.findAll();

        if (incidents.isEmpty()) {
            throw new NotFoundException("No incidents found in the system");
        }

        return incidents.stream().map(incident -> IncidentResponse.builder()
                .statusCode(200)
                .incidentNumber(incident.getIncidentNumber())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .priority(incident.getPriority())
                .imageUrl(incident.getImageUrl())
                .reportedByEmail(incident.getReportedByEmail())
                .location(incident.getLocation())
                .resolvingDepartment(incident.getResolvingDepartment())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build()
        ).toList();
    }



}
