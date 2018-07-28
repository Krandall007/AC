package com.rockyrunstream.ac.exception;


import org.springframework.http.HttpStatus;

/**
 * Base class for service exceptions
 */
public abstract class ServiceException extends RuntimeException {

    private int code;
    private HttpStatus httpStatus;

    ServiceException(String message, int code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    ServiceException(Throwable cause, int code, HttpStatus httpStatus) {
        super(cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
