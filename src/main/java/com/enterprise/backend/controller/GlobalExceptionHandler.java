package com.enterprise.backend.controller;

import com.enterprise.backend.exception.BannedUserException;
import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(EnterpriseBackendException.class)
    public ResponseEntity<ErrorResponse> handleEnglishException(EnterpriseBackendException e) {
        log.info(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e));
    }

    @ExceptionHandler(BannedUserException.class)
    public ResponseEntity<ErrorResponse> handleBannedUserException(BannedUserException e) {
        log.info(e.getMessage(), e);
        return ResponseEntity.status(ErrorCode.BANNED_PERFORM_ACTION.httpStatus()).body(new ErrorResponse(ErrorCode.BANNED_PERFORM_ACTION));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.info(e.getMessage(), e);
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.httpStatus()).body(new ErrorResponse(ErrorCode.ACCESS_DENIED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.info(message);
        ErrorResponse response = new ErrorResponse();
        response.setCode(ErrorCode.BAD_REQUEST.code());
        response.setDescription(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCode.INTERNAL_SERVER));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> parametersNotValid(BindException e) {
        log.error(e.getMessage(), e);
        var messageError = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        var fieldError = e.getBindingResult().getFieldErrors();
        if (!CollectionUtils.isEmpty(fieldError)) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.BAD_REQUEST, messageError.toString()));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error(e.getMessage(), e);
        if ("Banded perform action!".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCode.BANNED_USER));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCode.INVALID_USERNAME));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.info(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("APP-101");
        errorResponse.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
