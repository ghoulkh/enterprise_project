package com.enterprise.backend.exception;

import com.enterprise.backend.model.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EnterpriseBackendException extends RuntimeException {
    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    public EnterpriseBackendException(ErrorCode errorCode) {
        super(errorCode.description());
        this.code = errorCode.code();
        this.description = errorCode.description();
        this.httpStatus = errorCode.httpStatus();
    }

    public EnterpriseBackendException(ErrorCode errorCode, String description) {
        super(errorCode.description());
        this.code = errorCode.code();
        this.description = description;
        this.httpStatus = errorCode.httpStatus();
    }
}
