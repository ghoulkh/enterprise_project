package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.model.request.CategoryRequest;
import com.enterprise.backend.model.response.CategoryResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryTransformer extends BaseTransformer<Category, CategoryResponse, CategoryRequest> {
}
