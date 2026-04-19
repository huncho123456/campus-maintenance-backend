package com.praise.incident.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptionRequest {

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("prompt")
    private String prompt; // optional
}