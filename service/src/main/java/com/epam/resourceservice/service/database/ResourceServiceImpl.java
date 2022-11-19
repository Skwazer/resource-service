package com.epam.resourceservice.service.database;

import com.epam.resourceservice.dto.MultipleResourceDto;
import com.epam.resourceservice.dto.StorageDto;
import com.epam.resourceservice.exception.NotMp3FileException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.publisher.RMQPublisher;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.StorageServiceClient;
import com.epam.resourceservice.service.s3.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.amazonaws.services.mediaconvert.model.AudioCodec.MP3;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final FileUploadService fileUploadService;
    private final ResourceRepository repository;
    private final RMQPublisher publisher;
    private final StorageServiceClient storageServiceClient;

    public int upload(MultipartFile resource) {
        if (!validateIfMp3(resource)) {
            throw new NotMp3FileException("Provided resource is not of .mp3 format");
        }
        Resource resourceEntity = fileUploadService.uploadFileToStaging(resource);
        var resourceId = repository.save(resourceEntity).getId();
        log.info("File {} was saved to DB", resource.getName());
        publisher.publishCreationEvent(resourceId.toString());
        return resourceId;
    }

    @SneakyThrows
    public byte[] findResource(int id) {
        Resource resource = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource with id = " + id + " not found"));
        StorageDto storage = storageServiceClient.findStorage(resource.getStorageId());
        return fileUploadService.findFile(resource.getLocation(), storage.getBucketName());
    }

    @Transactional
    public MultipleResourceDto deleteResources(final List<Integer> ids) {
        var resources = ids.stream()
                .map(id -> repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Resource with id = " + id + " doesn't exist")))
                .collect(toList());
        resources.forEach(this::deleteResource);
        var response = new MultipleResourceDto();
        response.setIds(resources.stream().map(Resource::getId).collect(toList()));
        publisher.publishDeleteEvent(response.getIds().toString());
        return response;
    }

    private void deleteResource(final Resource resource) {
        StorageDto storage = storageServiceClient.findStorage(resource.getStorageId());
        fileUploadService.deleteFile(resource.getLocation(), storage.getBucketName());
        repository.delete(resource);
    }

    private boolean validateIfMp3(final MultipartFile resource) {
        return MP3.toString().equalsIgnoreCase(FilenameUtils.getExtension(resource.getResource().getFilename()));
    }

    public Integer moveToPermanentStorage(Integer id) {
        log.info("Moving resource with id: {} from staging to permanent storage", id);
        Resource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find resource with id: " + id));
        Integer permanentStorageId = fileUploadService.moveFileToPermanentStorage(resource.getLocation());
        resource.setStorageId(permanentStorageId.toString());
        repository.save(resource);
        return resource.getId();
    }
}
