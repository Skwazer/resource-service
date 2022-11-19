package com.epam.resourceservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StorageType {

    STAGING("staging"),
    PERMANENT("permanent");

    private final String name;

}
