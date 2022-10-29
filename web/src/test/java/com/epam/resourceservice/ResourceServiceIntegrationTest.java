package com.epam.resourceservice;

import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.SongService;
import com.epam.resourceservice.service.database.ResourceServiceImpl;
import com.epam.resourceservice.service.s3.FileUploadService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.amazonaws.services.mediaconvert.model.AudioCodec.MP3;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ResourceServiceApplication.class)
@Testcontainers
@ActiveProfiles("test")
class ResourceServiceIntegrationTest {

    @Container
    static RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
            .withQueue("Resources.Create")
            .withQueue("Resources.Delete");

    @MockBean
    FileUploadService fileUploadService;
    @MockBean
    ResourceRepository repository;
    @MockBean
    SongService songService;

    @Autowired
    RabbitTemplate template;

    @Autowired
    ResourceServiceImpl resourceService;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitContainer::getAmqpPort);
    }

    @Test
    void upload() {
        MockMultipartFile file = new MockMultipartFile("test_file.mp3", "test_file.mp3", MP3.toString(), new byte[] {});
        String path = "path";
        Integer expectedId = 100500;
        Resource resource = new Resource();
        resource.setLocation(path);
        resource.setId(expectedId);

        when(fileUploadService.uploadFile(file)).thenReturn(path);
        when(repository.save(any(Resource.class))).thenReturn(resource);

        resourceService.upload(file);

        Object message = template.receiveAndConvert("Resources.Create");
        Assertions.assertThat(message).isEqualTo(resource.getId().toString());
    }

}