package com.epam.resourceservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
public class ResourceServiceClientImpl implements ResourceServiceClient{

    @Value("${resource.service.baseUrl}")
    private String baseUrl;

    private final RestTemplate resourceServiceRestTemplate;

    @Override
    public ResponseEntity<ByteArrayResource> findResourceById(int id) {
        String idPath = String.valueOf(id);
        return resourceServiceRestTemplate.getForEntity(UriComponentsBuilder.fromHttpUrl(baseUrl).pathSegment(idPath).toUriString(), ByteArrayResource.class);
    }
}
