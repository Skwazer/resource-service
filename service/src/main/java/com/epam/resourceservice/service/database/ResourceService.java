package com.epam.resourceservice.service.database;

import org.springframework.web.multipart.MultipartFile;


public interface ResourceService {

    int upload(MultipartFile resource);
}
