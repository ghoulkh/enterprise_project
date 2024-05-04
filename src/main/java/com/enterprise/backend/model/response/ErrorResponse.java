package com.enterprise.backend.model.response;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.error.ErrorCode;
import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String description;

    public ErrorResponse(EnterpriseBackendException e) {
        this.code = e.getCode();
        this.description = e.getDescription();
    }

    public ErrorResponse(ErrorCode errorCode, String description) {
        this.code = errorCode.code();
        this.description = description;
    }

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.code();
        this.description = errorCode.description();
    }
}

