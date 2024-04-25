package com.enterprise.backend.model.request;

import com.enterprise.backend.model.entity.Orders;
import com.enterprise.backend.model.entity.ProductOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductOrderRequest {
    @NotNull(message = "receiverFullName is required!")
    private String receiverFullName;
    @NotNull(message = "email is required!")
    private String email;
    @NotNull(message = "phoneNumber is required!")
    private String phoneNumber;
    @NotNull(message = "addressDetail is required!")
    private String addressDetail;
    private String note;

    @NotNull(message = "products is required!")
    @NotEmpty(message = "products is required!")
    private List<@Valid OrderRequest> products;
    @NotNull(message = "products is required!")
    private String htmlContent;

    @Data
    public static class OrderRequest {
        @NotNull(message = "productId is required!")
        private Long productId;
        @NotNull(message = "quantity is required!")
        private Integer quantity;

    }

    public List<ProductOrder> toOrder() {
        List<ProductOrder> result = new ArrayList<>();
        for (OrderRequest orderRequest : products) {
            Orders toAdd = new Orders();
            BeanUtils.copyProperties(this, toAdd);
//            toAdd.setProductId(orderRequest.productId);
            toAdd.setQuantity(orderRequest.getQuantity());
//            result.add(toAdd);
        }
        return result;
    }
}
