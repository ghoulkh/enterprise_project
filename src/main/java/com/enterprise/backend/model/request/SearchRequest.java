package com.enterprise.backend.model.request;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.enterprise.backend.util.Utils.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SearchRequest {
    LocalDateTime fromCreatedDate;
    LocalDateTime toCreatedDate;
    LocalDateTime fromModifiedDate;
    LocalDateTime toModifiedDate;
    Integer pageSize = 10;
    Integer pageNumber = 0;
    String orderBy = "createdDate";
    Boolean isDesc = true;

    public void setToCreatedDate(String toCreatedDate) {
        if (StringUtils.isEmpty(toCreatedDate)) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        try {
            this.toCreatedDate = LocalDateTime.parse(toCreatedDate + TIME_END, formatter);
        } catch (Exception e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_DATE, e.getMessage());
        }
    }

    public void setFromCreatedDate(String fromCreatedDate) {
        if (StringUtils.isEmpty(fromCreatedDate)) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        try {
            this.fromCreatedDate = LocalDateTime.parse(fromCreatedDate + TIME_START, formatter);
        } catch (Exception e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_DATE, e.getMessage());
        }
    }

    public void setToModifiedDate(String toModifiedDate) {
        if (StringUtils.isEmpty(toModifiedDate)) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        try {
            this.toModifiedDate = LocalDateTime.parse(toModifiedDate + TIME_END, formatter);
        } catch (Exception e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_DATE, e.getMessage());
        }
    }

    public void setFromModifiedDate(String fromModifiedDate) {
        if (StringUtils.isEmpty(fromModifiedDate)) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        try {
            this.fromModifiedDate = LocalDateTime.parse(fromModifiedDate + TIME_START, formatter);
        } catch (Exception e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_DATE, e.getMessage());
        }
    }
}
