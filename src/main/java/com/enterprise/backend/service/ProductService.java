package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.model.entity.QProduct;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ProductRequest;
import com.enterprise.backend.model.request.SearchProductRequest;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.CategoryRepository;
import com.enterprise.backend.service.repository.ProductRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enterprise.backend.service.transfomer.ProductTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
@Slf4j
public class ProductService extends BaseService<Product, Long, ProductRepository, ProductTransformer, ProductRequest, ProductResponse> {
    private final ProductTransformer productTransformer;

    private static final QProduct qProduct = QProduct.product;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();

    private final CategoryRepository categoryRepository;

    protected ProductService(ProductRepository repo,
                             ProductTransformer transformer,
                             EntityManager em,
                             CategoryRepository categoryRepository,
                             ProductTransformer productTransformer) {
        super(repo, transformer, em);
        this.categoryRepository = categoryRepository;
        sortProperties.put(Product.Fields.id, qProduct.id);
        sortProperties.put(Product.Fields.price, qProduct.price);
        sortProperties.put(Product.Fields.rate, qProduct.rate);
        sortProperties.put(Auditable.Fields.createdDate, qProduct.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qProduct.updatedDate);
        this.productTransformer = productTransformer;
    }

    private void validateNameAndTitle(ProductRequest productRequest) {
        if (repo.countByNameOrTitle(productRequest.getName(), productRequest.getTitle()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.PRODUCT_CONFLICT);
        }
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        validateNameAndTitle(productRequest);
        Product product = transformer.toEntity(productRequest);

        executeCategory(productRequest, product);

        return transformer.toResponse(repo.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        Product product = getOrElseThrow(id);

        if (!(product.getName().equals(productRequest.getName())
                && product.getTitle().equals(productRequest.getTitle()))) {
            validateNameAndTitle(productRequest);
        }

        BeanUtils.copyProperties(productRequest, product);
        executeCategory(productRequest, product);

        return transformer.toResponse(repo.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getOrElseThrow(id);
        repo.delete(product);
    }

    public ProductResponse getById(Long id) {
        Product product = getOrElseThrow(id);
        return transformer.toResponse(product);
    }

    public Page<ProductResponse> search(SearchProductRequest searchProductRequest) {
        PageRequest of = PageRequest.of(searchProductRequest.getPageNumber(), searchProductRequest.getPageSize());
        JPAQuery<Product> search = searchQuery(searchProductRequest);
        log.info("searchProduct query: {}", search);

        List<ProductResponse> responses = search.fetch()
                .stream()
                .map(productTransformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, responses.size());
    }

    private JPAQuery<Product> searchQuery(SearchProductRequest searchRequest) {
        JPAQuery<Product> query = queryFactory.selectFrom(qProduct);

        queryPage(searchRequest, query);

        if (ObjectUtils.isNotEmpty(searchRequest.getProductId())) {
            query.where(qProduct.id.eq(searchRequest.getProductId()));
        }

        if (StringUtils.isNotEmpty(searchRequest.getKeyword())) {
            query.where(qProduct.title.containsIgnoreCase(searchRequest.getKeyword())
                    .or(qProduct.name.containsIgnoreCase(searchRequest.getKeyword())));
        }

        if (ObjectUtils.isNotEmpty(searchRequest.getFromPrice())) {
            query.where(qProduct.price.goe(searchRequest.getFromPrice()));
        }

        if (ObjectUtils.isNotEmpty(searchRequest.getToPrice())) {
            query.where(qProduct.price.loe(searchRequest.getToPrice()));
        }

        if (searchRequest.getFromCreatedDate() != null) {
            query.where(qProduct.createdDate.goe(searchRequest.getFromCreatedDate()));
        }

        if (searchRequest.getToCreatedDate() != null) {
            query.where(qProduct.createdDate.loe(searchRequest.getToCreatedDate()));
        }

        sortBy(searchRequest.getOrderBy(), searchRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    private void executeCategory(ProductRequest productRequest, Product product) {
        if (!CollectionUtils.isEmpty(productRequest.getCategoryIds())) {
            List<Category> categories = categoryRepository.findAllById(productRequest.getCategoryIds());
            categories.forEach(category -> category.getProducts().add(product));
            if (!CollectionUtils.isEmpty(categories)) {
                product.setCategories(new HashSet<>(categories));
            }
            categoryRepository.saveAll(categories);
        }
    }

    @Override
    protected String notFoundMessage() {
        return "Not found product";
    }
}
