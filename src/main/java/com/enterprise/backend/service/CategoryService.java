package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.CategoryRequest;
import com.enterprise.backend.model.response.CategoryResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.CategoryRepository;
import com.enterprise.backend.service.transfomer.CategoryTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService extends BaseService<Category, Long, CategoryRepository, CategoryTransformer, CategoryRequest, CategoryResponse> {
    private final CategoryTransformer categoryTransformer;

    protected CategoryService(CategoryRepository repo,
                              CategoryTransformer transformer,
                              EntityManager em,
                              CategoryTransformer categoryTransformer) {
        super(repo, transformer, em);
        this.categoryTransformer = categoryTransformer;
    }

    private void validateName(CategoryRequest categoryRequest) {
        if (repo.countByName(categoryRequest.getName()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.CATEGORY_CONFLICT);
        }
    }

    private void validatePriority(CategoryRequest categoryRequest) {
        if (repo.countByPriority(categoryRequest.getPriority()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_PRIORITY);
        }
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        validateName(categoryRequest);
        validatePriority(categoryRequest);
        Category category = transformer.toEntity(categoryRequest);
        return transformer.toResponse(repo.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long id) {
        Category category = getOrElseThrow(id);

        if (!(category.getName().equals(categoryRequest.getName()))) {
            validateName(categoryRequest);
        }

        if (!(category.getPriority().equals(categoryRequest.getPriority()))
                && ObjectUtils.isNotEmpty(categoryRequest.getPriority())) {
            repo.findByPriority(categoryRequest.getPriority()).ifPresent(categoryReplace -> {
                categoryReplace.setPriority(category.getPriority());
                repo.save(categoryReplace);
            });
        }

        BeanUtils.copyProperties(categoryRequest, category);
        return transformer.toResponse(repo.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getOrElseThrow(id);
        repo.delete(category);
    }

    public List<CategoryResponse> getAllSortByPriority() {
        return repo.findByOrderByPriorityAsc()
                .stream()
                .map(categoryTransformer::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    protected String notFoundMessage() {
        return "Not found category";
    }
}
