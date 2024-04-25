package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.request.ProductRequest;
import com.enterprise.backend.model.request.UserRequest;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface ProductTransformer extends BaseTransformer<Product, ProductResponse, ProductRequest> {
}
