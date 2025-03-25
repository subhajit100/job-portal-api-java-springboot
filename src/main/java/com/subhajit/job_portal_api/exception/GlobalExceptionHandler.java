package com.subhajit.job_portal_api.exception;

import com.subhajit.job_portal_api.dto.ErrorResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now(), request.getDescription(false));
        if(ex instanceof UserNotFoundException || ex instanceof JobNotFoundException || ex instanceof ApplicationNotFoundException){
            return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
        }
        else if(ex instanceof UnauthorizedAccessException){
            return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
        }
        else{
            return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorMessage,LocalDateTime.now(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
}
