package com.rockyrunstream.ac.exception;

import org.springframework.http.HttpStatus;

/**
 * Optimistic lock approach failed
 */
public class OptimisticLockException extends ServiceException {

    private static final int CODE = 2;

    public OptimisticLockException(String message) {
        super(message, CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
