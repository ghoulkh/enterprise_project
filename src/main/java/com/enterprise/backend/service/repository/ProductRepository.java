package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends BaseCommonRepository<Product, Long> {
    Integer countByTitle(String title);
}
