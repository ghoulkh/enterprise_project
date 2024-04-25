package com.enterprise.backend.model.response;

import lombok.Data;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rate;
    private String comment;
    private String fullName;
    private String userId;
}
