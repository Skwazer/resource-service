package com.epam.resourceservice.config;

import com.epam.songservice.client.SongServiceClient;
import com.epam.songservice.client.SongServiceClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author www.epam.com
 */
@Configuration
@Import(com.epam.songservice.config.ClientConfig.class)
public class ClientConfig {

    @Bean
    public SongServiceClient songServiceClient(RestTemplate songServiceRestTemplate) {
        return new SongServiceClientImpl(songServiceRestTemplate);
    }
}
