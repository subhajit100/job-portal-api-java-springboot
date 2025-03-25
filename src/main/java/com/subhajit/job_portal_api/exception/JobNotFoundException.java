package com.subhajit.job_portal_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class JobNotFoundException extends RuntimeException {
  public JobNotFoundException(String message) {
    super(message);
  }
}
