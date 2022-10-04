package com.epam.resourceservice.client;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

/**
 * @author www.epam.com
 */
public interface ResourceServiceClient {
    public ResponseEntity<ByteArrayResource> findResourceById(int id);
}
