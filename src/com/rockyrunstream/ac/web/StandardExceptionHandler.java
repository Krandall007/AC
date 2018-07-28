package com.rockyrunstream.ac.web;

import com.rockyrunstream.ac.exception.BadRequest;
import com.rockyrunstream.ac.exception.InternalServerException;
import com.rockyrunstream.ac.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class StandardExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StandardExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception e) {
        log.error(e.getMessage(), e);
        return wrapError(new InternalServerException(e));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object handle(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return wrapError(new BadRequest(e));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object handle(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return wrapError(new BadRequest(e));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Object handle(ServiceException e) {
        return wrapError(e);
    }


    private ResponseEntity wrapError(ServiceException e) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ServiceError error = new ServiceError();
        error.setMessage(e.getMessage());
        error.setError(e.getCode());

        return new ResponseEntity<>(error, headers, e.getHttpStatus());
    }
}
