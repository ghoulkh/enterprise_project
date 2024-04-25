package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends BaseCommonRepository<Category, Long> {
    long countByName(String name);
    long countByPriority(Integer priority);
    Optional<Category> findByPriority(Integer priority);
    List<Category> findByOrderByPriorityAsc();
}
