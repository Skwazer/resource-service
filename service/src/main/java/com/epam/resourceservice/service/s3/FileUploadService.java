package com.epam.resourceservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @author www.epam.com
 */
@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @SneakyThrows
    public String uploadFile(final MultipartFile file) {
        var fileName = LocalDateTime.now() + "-" + FilenameUtils.removeExtension(file.getResource().getFilename());
        var resourceMetadata = new ObjectMetadata();
        resourceMetadata.setContentLength(file.getResource().contentLength());
        s3Client.putObject(bucketName, fileName, file.getInputStream(), resourceMetadata);
        return fileName;
    }

    @SneakyThrows
    public byte[] findFile(final String path) {
        return s3Client.getObject(bucketName, path).getObjectContent().getDelegateStream().readAllBytes();
    }

    public void deleteFile(final String path) {
        s3Client.deleteObject(bucketName, path);
    }
}
