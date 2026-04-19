package com.praise.incident.service;

import com.praise.incident.client.MLServiceClient;
import com.praise.incident.dto.CaptionRequest;
import com.praise.incident.dto.CaptionResponse;
import com.praise.incident.enums.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final MLServiceClient mlServiceClient;

    public CaptionResponse analyzeImage(String imageUrl, String prompt) {
        CaptionRequest request = CaptionRequest.builder()
                .imageUrl(imageUrl)
                .prompt(prompt)
                .build();

        log.info("Sending image for analysis: {}", imageUrl);
        CaptionResponse response = mlServiceClient.analyzeImage(request);
        log.info("Received department: {} with confidence: {}", response.getDepartment(), response.getConfidence());

        return response;
    }

    public Department resolveDepartment(String imageUrl, String prompt) {
        CaptionResponse response = analyzeImage(imageUrl, prompt);
        return mapToDepartmentEnum(response.getDepartment());
    }

    private Department mapToDepartmentEnum(String departmentLabel) {
        return switch (departmentLabel) {
            case "Physical Planning and Development Unit"    -> Department.PPDU;
            case "Electrical Maintenance Unit"               -> Department.ELECTRICAL_MAINTENANCE;
            case "Plumbing and Water Maintenance Unit"       -> Department.PLUMBING_AND_WATER;
            case "Environmental Health and Sanitation Unit"  -> Department.ENVIRONMENTAL_SANITATION;
            case "ICT / Network Support Unit"                -> Department.ICT_NETWORK_SUPPORT;
            case "Hostel Maintenance Unit"                   -> Department.HOSTEL_MAINTENANCE;
            case "Grounds and Drainage Maintenance Unit"     -> Department.GROUNDS_AND_DRAINAGE;
            default -> throw new IllegalArgumentException("Unknown department: " + departmentLabel);
        };
    }
}