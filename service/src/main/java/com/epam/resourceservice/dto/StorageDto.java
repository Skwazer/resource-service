package com.epam.resourceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Value;


@Value
@AllArgsConstructor
public class StorageDto {

    Integer id;
    StorageType type;
    String bucketName;
    String path;

}
