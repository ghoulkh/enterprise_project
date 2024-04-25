package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.List;

public interface CategoryRepository extends BaseCommonRepository<Category, Long> {
    long countByName(String name);

    List<Category> findByOrderByPriorityAsc();
}
