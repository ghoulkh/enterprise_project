package com.enterprise.backend.model.request;

import javax.validation.constraints.Min;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class ProductRequest {
    @NotEmpty(message = "title is required!")
    private String title;

    @NotEmpty(message = "description is required!")
    private String description;

    @NotNull(message = "imagesUrl is required!")
    private List<String> imagesUrl;

    @Min(value = 0, message = "Giá tiền không thể âm!")
    @NotNull(message = "price is required!")
    private Long price;

    @NotNull(message = "quantity is required!")
    private Long quantity;

    private Set<Long> categoryIds;
}
