package com.epam.resourceservice.service.database;

import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.publisher.RMQPublisher;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.SongService;
import com.epam.resourceservice.service.s3.FileUploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static com.amazonaws.services.mediaconvert.model.AudioCodec.MP3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    FileUploadService fileUploadService;
    @Mock
    ResourceRepository repository;
    @Mock
    RMQPublisher publisher;
    @Mock
    SongService songService;

    @InjectMocks
    ResourceServiceImpl resourceService;

    @Test
    void upload() {
        MockMultipartFile file = new MockMultipartFile("test_file.mp3", "test_file.mp3", MP3.toString(), new byte[] {});
        String path = "path";
        Integer expectedId = 1;
        Resource resource = new Resource();
        resource.setLocation(path);
        resource.setId(expectedId);

        when(fileUploadService.uploadFileToStaging(file)).thenReturn(resource);
        when(repository.save(any(Resource.class))).thenReturn(resource);

        int actualId = resourceService.upload(file);
        verify(publisher).publishCreationEvent(expectedId.toString());
        assertEquals(expectedId, actualId);
    }

}