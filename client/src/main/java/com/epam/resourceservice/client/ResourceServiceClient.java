package com.epam.resourceservice.client;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;


public interface ResourceServiceClient {
    public ResponseEntity<ByteArrayResource> findResourceById(int id);
}
