package com.epam.resourceservice.service;

import com.epam.resourceservice.dto.StorageDto;
import com.epam.resourceservice.dto.StorageType;
import com.epam.resourceservice.exception.StorageNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceClient {

    private final RestTemplate restTemplate;

    @Value("${storage-service.baseUrl}")
    private String baseUrl;

    @Value("${storage-service.storages.staging}")
    private String stagingStorageName;

    @Value("${storage-service.storages.permanent}")
    private String permanentStorageName;

    @CircuitBreaker(name = "CircuitBreakerService", fallbackMethod = "fallback")
    public StorageDto getStagingStorage() {
        return getStorage(stagingStorageName, StorageType.STAGING);
    }

    private StorageDto fallback(Throwable throwable) {
        log.error("Exception occurred when tried to get staging storage", throwable);
        log.error("Fallback method, returning stub staging storage");
        return new StorageDto(1, StorageType.STAGING, "staging-storage", "/staging-storage");
    }

    public StorageDto getPermanentStorage() {
        return getStorage(permanentStorageName, StorageType.PERMANENT);
    }

    @SuppressWarnings("unchecked")
    public StorageDto getStorage(String storageName, StorageType storageType) {
        try {
            log.info("Trying to get storages info from {}", baseUrl);
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).toUriString();
            StorageDto[] storages = restTemplate.getForEntity(url, StorageDto[].class).getBody();
            if (isNull(storages) || storages.length == 0) {
                throw new StorageNotFoundException("Storage service does not have available storages");
            }

            StorageDto storage = findStorage(storageName, storageType, storages);
            log.info("Success");
            return storage;
        } catch (Exception ex) {
            log.error("Storage service is not available...", ex);
            throw new StorageNotFoundException("Error while calling song service", ex);
        }
    }

    private StorageDto findStorage(String storageName, StorageType storageType, StorageDto[] storages) {
        return Stream.of(storages)
                .filter(storage -> storageType == storage.getType())
                .filter(storage -> storageName.equals(storage.getBucketName()))
                .findAny()
                .orElseThrow(() -> new StorageNotFoundException("Storage service cannot find storage: " + storageName));
    }

    public StorageDto findStorage(String storageId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl).pathSegment(storageId).toUriString();
        StorageDto storage = restTemplate.getForEntity(url, StorageDto.class).getBody();
        if (isNull(storage)) {
            log.error("Storage service cannot find storage with id {}", storageId);
            throw new StorageNotFoundException("Storage service cannot find storage with id: " + storageId);
        }
        return storage;
    }
}
