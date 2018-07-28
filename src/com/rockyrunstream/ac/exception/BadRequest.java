package com.rockyrunstream.ac.exception;

import org.springframework.http.HttpStatus;

public class BadRequest extends ServiceException {

    private static final int CODE = 1;

    public BadRequest(Throwable cause) {
        super(cause, CODE, HttpStatus.OK);
    }

}
