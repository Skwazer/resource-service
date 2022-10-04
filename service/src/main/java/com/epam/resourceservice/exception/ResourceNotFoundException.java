package com.epam.resourceservice.exception;

/**
 * @author www.epam.com
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
