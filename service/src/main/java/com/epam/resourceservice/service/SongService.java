package com.epam.resourceservice.service;

import com.epam.resourceservice.exception.SongServiceException;
import com.epam.songservice.client.SongServiceClient;
import com.epam.songservice.dto.SongMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * @author www.epam.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {
    private final SongServiceClient client;

    @Retryable(value = {SongServiceException.class}, maxAttempts = 2, backoff = @Backoff(delay = 100))
    public SongMetadataDto findSongMetadata(int resourceId) {
        try {
            log.info("Log attempt .... ");
            return client.findSongMetadataByResourceId(resourceId);
        } catch (Exception ex) {
            throw new SongServiceException("Error while calling song service", ex);
        }
    }

}
