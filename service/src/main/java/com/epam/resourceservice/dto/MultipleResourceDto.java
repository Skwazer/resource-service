package com.epam.resourceservice.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author www.epam.com
 */
@Data
@ToString
public class MultipleResourceDto {
    private List<Integer> ids;
}
