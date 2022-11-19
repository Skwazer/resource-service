package com.epam.resourceservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.epam.resourceservice.dto.StorageDto;
import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.service.StorageServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AmazonS3 s3Client;
    private final StorageServiceClient storageServiceClient;

    @SneakyThrows
    public Resource uploadFileToStaging(final MultipartFile file) {
        var fileName = LocalDateTime.now() + "-" + FilenameUtils.removeExtension(file.getResource().getFilename());
        var resourceMetadata = new ObjectMetadata();
        resourceMetadata.setContentLength(file.getResource().contentLength());
        StorageDto stagingStorage = storageServiceClient.getStagingStorage();
        s3Client.putObject(stagingStorage.getBucketName(), fileName, file.getInputStream(), resourceMetadata);
        log.info("File {} was uploaded to s3 staging-storage bucket", fileName);

        var resource = new Resource();
        resource.setLocation(fileName);
        resource.setStorageId(stagingStorage.getId().toString());
        return resource;
    }

    @SneakyThrows
    public Integer moveFileToPermanentStorage(String fileName) {
        String stagingBucketName = storageServiceClient.getStagingStorage().getBucketName();
        StorageDto permanentStorage = storageServiceClient.getPermanentStorage();
        String permanentBucketName = permanentStorage.getBucketName();

        s3Client.copyObject(stagingBucketName, fileName, permanentBucketName, fileName);
        log.info("File {} has been copied from {} bucket to {} bucket", fileName, stagingBucketName, permanentBucketName);

        s3Client.deleteObject(stagingBucketName, fileName);
        log.info("File {} has been deleted from {} bucket", fileName, stagingBucketName);

        return permanentStorage.getId();
    }


    @SneakyThrows
    public byte[] findFile(String fileName, String bucketName) {
        return s3Client.getObject(bucketName, fileName).getObjectContent().getDelegateStream().readAllBytes();
    }

    public void deleteFile(final String fileName, String bucketName) {
        s3Client.deleteObject(bucketName, fileName);
    }

}
