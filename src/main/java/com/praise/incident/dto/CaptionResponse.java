package com.praise.incident.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CaptionResponse {

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("department")
    private String department;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("all_scores")
    private Map<String, Double> allScores;
}