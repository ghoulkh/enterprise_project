package com.enterprise.backend.model.response;

import lombok.Data;

import java.util.Set;

@Data
public class ProductOrderResponse {
    private Long id;
    private String receiverFullName;
    private String email;
    private String phoneNumber;
    private String addressDetail;
    private String note;
    private Integer quantity;
    private Long price;
    private String status;
    private String type;
    private Set<OrderResponse> orders;

    @Data
    public static class OrderResponse {
        private ProductResponse product;
        private Integer quantity;
    }
}
