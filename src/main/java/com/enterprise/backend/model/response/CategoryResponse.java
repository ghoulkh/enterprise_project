package com.enterprise.backend.model.response;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private Integer priority;
}
