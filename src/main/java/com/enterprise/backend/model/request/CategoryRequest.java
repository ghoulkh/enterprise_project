package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class CategoryRequest {
    @NotEmpty(message = "name is required!")
    private String name;

    @Min(value = 0, message = "Thứ tự ưu tiên không thể âm!")
    private Integer priority;

}
