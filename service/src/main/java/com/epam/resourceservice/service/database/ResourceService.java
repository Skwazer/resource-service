package com.epam.resourceservice.service.database;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author www.epam.com
 */
public interface ResourceService {

    int upload(MultipartFile resource);
}
