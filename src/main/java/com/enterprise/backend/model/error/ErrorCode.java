package com.enterprise.backend.model.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ACCESS_DENIED("APP-00", "Xin lỗi, bạn không có quyền truy cập!", HttpStatus.FORBIDDEN),
    BAD_REQUEST("APP-01", "Yêu cầu của bạn chưa đúng!!", HttpStatus.BAD_REQUEST),
    NOT_FOUND("APP-02", "Not found entity!!", HttpStatus.BAD_REQUEST),
    CONFLICT("APP-03", "Not found entity!!", HttpStatus.CONFLICT),
    UNAUTHORIZED("APP-UNAUTHORIZED", "Phiên làm việc hết hạn!", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER("APP-INTERNAL-SERVER", "internal server!", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(NOT_FOUND.code, "Không tìm thấy người dùng!", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(BAD_REQUEST.code, "Sai username", HttpStatus.BAD_REQUEST),
    BANNED_PERFORM_ACTION(UNAUTHORIZED.code, "Banded perform action!", HttpStatus.UNAUTHORIZED),
    POST_NOT_FOUND(NOT_FOUND.code, "Không tìm thấy bài viết!", HttpStatus.NOT_FOUND),
    CONFLICT_USERNAME(CONFLICT.code, "Conflict username!", HttpStatus.CONFLICT),
    INVALID_USERNAME_OR_PASSWORD(BAD_REQUEST.code, "Invalid username or password!", HttpStatus.BAD_REQUEST),
    BANNED_USER(ACCESS_DENIED.code, "User is banned", HttpStatus.FORBIDDEN),
    CODE_INVALID(BAD_REQUEST.code, "Code invalid!", HttpStatus.BAD_REQUEST),
    CODE_IS_EXPIRE(BAD_REQUEST.code, "Code is expire!", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(NOT_FOUND.code, "Product not found!", HttpStatus.NOT_FOUND),
    PRODUCT_CONFLICT(NOT_FOUND.code, "Product name or title is existed!", HttpStatus.CONFLICT),
    CATEGORY_CONFLICT(NOT_FOUND.code, "Category name is existed!", HttpStatus.CONFLICT),
    CONFLICT_EMAIL(CONFLICT.code, "Trùng Emai!", HttpStatus.CONFLICT),
    CONFLICT_PHONE(CONFLICT.code, "Trùng số điện thoại!", HttpStatus.CONFLICT),
    ORDER_NOT_FOUND(NOT_FOUND.code, "order not found!", HttpStatus.NOT_FOUND),
    INVALID_ORDER(BAD_REQUEST.code, "Số thứ tự trong khoảng thừ 1 đến 6", HttpStatus.BAD_REQUEST),
    PRICE_INVALID(BAD_REQUEST.code, "Giá tiền không được âm!", HttpStatus.BAD_REQUEST),
    INVALID_DATE(BAD_REQUEST.code, "Invalid date. Date must be dd/mm/yyyy!", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return this.code;
    }
    public String description() {
        return this.description;
    }
    public HttpStatus httpStatus() {
        return this.httpStatus;
    }
}

