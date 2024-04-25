package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ProductOrderRequest {
    @NotEmpty(message = "receiverFullName is required!")
    private String receiverFullName;
    @NotEmpty(message = "email is required!")
    private String email;
    @NotEmpty(message = "phoneNumber is required!")
    private String phoneNumber;
    @NotEmpty(message = "addressDetail is required!")
    private String addressDetail;
    private String note;
    private String userId;

    @NotNull(message = "products is required!")
    private List<@Valid OrderRequest> products;
    @NotNull(message = "products is required!")
    private String htmlContent;

    @Data
    public static class OrderRequest {
        @NotEmpty(message = "productId is required!")
        private Long productId;
        @NotNull(message = "quantity is required!")
        private Integer quantity;
    }
}
