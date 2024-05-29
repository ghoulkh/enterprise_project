package com.enterprise.backend.model.response;

import java.util.List;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private List<String> imagesUrl;
    private Long price;
    private Long quantity;
    private Double rate;
    private String createdBy;
    private String updatedBy;
    private String createdDate;
    private String updatedDate;
    private List<OfCategory> ofCategories;

    @Data
    public static class OfCategory {
        private Long id;
        private String name;
        private Integer priority;
    }
}
