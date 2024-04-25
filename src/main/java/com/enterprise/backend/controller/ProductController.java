package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.request.*;
import com.enterprise.backend.model.response.ProductOrderResponse;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.model.response.ReviewResponse;
import com.enterprise.backend.service.ProductOrderService;
import com.enterprise.backend.service.ProductService;
import com.enterprise.backend.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductOrderService productOrderService;
    private final ReviewService reviewService;

    @PostMapping
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> search(@ModelAttribute @Valid SearchProductRequest searchProductRequest) {
        return ResponseEntity.ok(productService.search(searchProductRequest));
    }

    @PatchMapping("/{productId}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest request,
                                                         @PathVariable Long productId) {
        return ResponseEntity.ok(productService.updateProduct(request, productId));
    }

    @DeleteMapping("/{productId}")
    @Transactional
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<String> deleteById(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/get-by-id/{productId}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getById(productId));
    }

    @PostMapping("/order")
    public void order(@RequestBody @Valid ProductOrderRequest request) {
        productService.orderProduct(request);
    }

    @PatchMapping("/order/{productOrderId}:{status}")
    public void updateOrderStatus(@PathVariable Long productOrderId, @PathVariable OrderStatus status) {
        productService.updateOrderStatus(productOrderId, status);
    }

    @GetMapping("/order/get-by-me")
    public Page<ProductOrderResponse> getProductOrderByMe(@ModelAttribute SearchProductOrderRequest searchRequest) {
        return productOrderService.getProductOrderByMe(searchRequest);
    }

    @GetMapping("/order/admin-search")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    public Page<ProductOrderResponse> adminSearchProductOrder(@ModelAttribute SearchProductOrderRequest searchRequest) {
        return productOrderService.adminSearchProductOrder(searchRequest);
    }

    @GetMapping("/review/{productId}")
    public Page<ReviewResponse> getProductOrderByMe(@ModelAttribute SearchRequest searchRequest,
                                                    @PathVariable Long productId) {
        return reviewService.getReviewByProduct(searchRequest, productId);
    }

}
