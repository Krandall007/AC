package com.rockyrunstream.ac.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ServiceException {

    private static final int CODE = 4;

    public InternalServerException(Throwable cause) {
        super(cause, CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
