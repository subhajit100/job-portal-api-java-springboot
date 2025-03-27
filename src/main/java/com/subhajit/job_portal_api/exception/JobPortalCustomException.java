package com.subhajit.job_portal_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JobPortalCustomException extends RuntimeException {
    private final HttpStatus httpStatus;
    public JobPortalCustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
