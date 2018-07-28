package com.rockyrunstream.ac.exception;

import org.springframework.http.HttpStatus;

public class QueueIsEmpty extends ServiceException {

    private static final int CODE = 3;

    public QueueIsEmpty(String message) {
        super(message, CODE, HttpStatus.OK);
    }
}
