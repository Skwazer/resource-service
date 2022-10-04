package com.epam.resourceservice.exception;

/**
 * @author www.epam.com
 */
public class SongServiceException extends RuntimeException{
    public SongServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
