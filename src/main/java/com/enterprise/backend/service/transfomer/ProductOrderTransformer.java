package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.ProductOrder;
import com.enterprise.backend.model.request.ProductOrderRequest;
import com.enterprise.backend.model.response.ProductOrderResponse;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Mapper
public interface ProductOrderTransformer extends BaseTransformer<ProductOrder, ProductOrderResponse, ProductOrderRequest> {
    @Override
    ProductOrderResponse toResponse(ProductOrder entity);

    @AfterMapping
    default void afterMappingToResponse(ProductOrder entity, @MappingTarget ProductOrderResponse response) {
        Set<ProductOrderResponse.OrderResponse> orders = new HashSet<>();
        entity.getOrders().forEach(order -> {
            ProductResponse product = new ProductResponse();
            product.setId(order.getProduct().getId());
            product.setDescription(order.getProduct().getDescription());
            product.setRate(order.getProduct().getRate());
            product.setTitle(order.getProduct().getTitle());
            product.setPrice(order.getProduct().getPrice());
            product.setQuantity(order.getProduct().getQuantity());
            product.setImagesUrl(order.getProduct().getImagesUrl());
            product.setCreatedBy(order.getProduct().getCreatedBy());
            product.setUpdatedBy(order.getProduct().getUpdatedBy());
            product.setCreatedDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(order.getProduct().getCreatedDate()));
            product.setUpdatedDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(order.getProduct().getUpdatedDate()));

            ProductOrderResponse.OrderResponse orderResponse = new ProductOrderResponse.OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setProduct(product);
            orderResponse.setQuantity(order.getQuantity());
            orders.add(orderResponse);
        });
        response.setOrders(orders);
    }
}
