package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.model.entity.Review;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.List;

public interface ReviewRepository extends BaseCommonRepository<Review, Long> {
    List<Review> findAllByProduct(Product product);
}
