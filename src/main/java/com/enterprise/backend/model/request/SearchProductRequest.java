package com.enterprise.backend.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchProductRequest extends SearchRequest {
    private Long productId;
    private String keyword;
    private Long fromPrice;
    private Long toPrice;
}
