package com.enterprise.backend.model.response;

import lombok.Data;

import java.util.*;

@Data
public class OrderResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private String receiverFullName;
    private String email;
    private String phoneNumber;
    private String addressDetail;
    private String note;
    private Date lastUpdated;
    private Date createdAt;
    private String createdBy;
    private String updatedBy;
    private Long productId;

}
