package com.praise.incident.controller;

import com.praise.incident.dto.IncidentDto;
import com.praise.incident.dto.IncidentResponse;
import com.praise.incident.security.JwtUtils;
import com.praise.incident.service.IncidentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/incident")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<IncidentResponse> createIncident(
            @RequestPart("data") IncidentDto incidentDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.getUsernameFromToken(token);
        log.info("email: {}", email);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.createIncident(incidentDto, file, email));
    }

    @GetMapping("/my-incidents")
    public ResponseEntity<List<IncidentResponse>> getMyIncidents(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(incidentService.getIncidentsByEmail(email));
    }

    @PutMapping("/{incidentNumber}")
    public ResponseEntity<IncidentResponse> updateIncident(
            @PathVariable String incidentNumber,
            @RequestPart("data") IncidentDto request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(incidentService.updateIncident(incidentNumber, request, file));
    }

    @DeleteMapping("/{incidentNumber}")
    public ResponseEntity<String> deleteIncident(@PathVariable String incidentNumber) {
        incidentService.deleteIncident(incidentNumber);
        return ResponseEntity.ok("Incident " + incidentNumber + " deleted successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

}
