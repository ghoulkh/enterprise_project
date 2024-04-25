package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.*;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ReviewRequest;
import com.enterprise.backend.model.request.SearchRequest;
import com.enterprise.backend.model.response.ReviewResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.ProductRepository;
import com.enterprise.backend.service.repository.ReviewRepository;
import com.enterprise.backend.service.transfomer.ReviewTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService extends BaseService<Review, Long, ReviewRepository, ReviewTransformer, ReviewRequest, ReviewResponse> {

    private static final QReview qReview = QReview.review;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();

    private final ProductRepository productRepository;

    protected ReviewService(ReviewRepository repo,
                            ReviewTransformer transformer,
                            EntityManager em,
                            ProductRepository productRepository) {
        super(repo, transformer, em);
        this.productRepository = productRepository;
        sortProperties.put(Product.Fields.id, qReview.id);
        sortProperties.put(Auditable.Fields.createdDate, qReview.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qReview.updatedDate);
    }

    public Page<ReviewResponse> getReviewByProduct(SearchRequest searchRequest, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        PageRequest of = PageRequest.of(searchRequest.getPageNumber(), searchRequest.getPageSize());
        JPAQuery<Review> search = searchQuery(searchRequest, product.getId());
        log.info("searchReview by product query: {}", search);

        List<ReviewResponse> responses = search.fetch()
                .stream()
                .map(transformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, responses.size());
    }

    private JPAQuery<Review> searchQuery(SearchRequest searchRequest, Long productId) {
        JPAQuery<Review> query = queryFactory.selectFrom(qReview);

        if (ObjectUtils.isNotEmpty(productId)) {
            QProduct qProduct = QProduct.product;
            query.innerJoin(qReview.product, qProduct)
                    .where(qProduct.id.eq(productId));
        }

        if (searchRequest.getToCreatedDate() != null) {
            query.where(qReview.createdDate.loe(searchRequest.getToCreatedDate()));
        }

        if (searchRequest.getToModifiedDate() != null) {
            query.where(qReview.updatedDate.loe(searchRequest.getToModifiedDate()));
        }

        if (searchRequest.getFromModifiedDate() != null) {
            query.where(qReview.updatedDate.goe(searchRequest.getFromModifiedDate()));
        }

        if (searchRequest.getFromCreatedDate() != null) {
            query.where(qReview.createdDate.goe(searchRequest.getFromCreatedDate()));
        }

        queryPage(searchRequest, query);
        sortBy(searchRequest.getOrderBy(), searchRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    @Override
    protected String notFoundMessage() {
        return "Not found product";
    }
}
