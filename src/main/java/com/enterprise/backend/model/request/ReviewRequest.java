package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ReviewRequest {
    @Min(value = 1, message = "Rate from 1 to 5")
    @Max(value = 5, message = "Rate from 1 to 5")
    private Integer rate;

    @NotEmpty(message = "Comment must not be empty")
    private String comment;

    @NotNull(message = "ProductId must not be null")
    private Long productId;

    private String userId;
}
