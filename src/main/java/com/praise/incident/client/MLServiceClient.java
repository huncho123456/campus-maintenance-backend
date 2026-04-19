package com.praise.incident.client;

import com.praise.incident.dto.CaptionRequest;
import com.praise.incident.dto.CaptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ml-service", url = "${ml.service.url}")
public interface MLServiceClient {

    @PostMapping("/caption")
    CaptionResponse analyzeImage(@RequestBody CaptionRequest request);
}