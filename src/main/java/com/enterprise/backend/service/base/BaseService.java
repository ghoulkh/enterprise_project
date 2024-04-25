package com.enterprise.backend.service.base;

import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.request.SearchRequest;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;

public abstract class BaseService<E extends Auditable, ID, R extends BaseCommonRepository<E, ID>, TF extends BaseTransformer<E, RP, RQ>, RQ, RP>
        extends BaseCommonRepoService<E, ID, R> {

    protected final TF transformer;
    protected final JPAQueryFactory queryFactory;

    protected BaseService(R repo, TF transformer, EntityManager em) {
        super(repo);
        this.transformer = transformer;
        this.queryFactory = new JPAQueryFactory(em);
    }

    protected void sortBy(String orderBy, Boolean isDesc, JPAQuery<E> query, Map<String, ComparableExpressionBase<?>> sortProperties) {
        if (orderBy != null && sortProperties.containsKey(orderBy)) {
            if (Boolean.TRUE.equals(isDesc)) {
                query.orderBy(sortProperties.get(orderBy).desc());
            } else {
                query.orderBy(sortProperties.get(orderBy).asc());
            }
        }
    }

    protected void queryPage(SearchRequest searchRequest, JPAQuery<E> query) {
        if (searchRequest.getPageNumber() != null) {
            int page;
            if (searchRequest.getPageNumber() <= 0) {
                page = 0;
            } else {
                page = searchRequest.getPageNumber() * searchRequest.getPageSize();
            }
            query.offset(page);
        }

        if (searchRequest.getPageSize() != null) {
            if (searchRequest.getPageSize() <= 0) {
                searchRequest.setPageSize(10);
            }
            query.limit(searchRequest.getPageSize());
        }
    }

    @Transactional
    public RP save(RQ request) {
        E entity = save(transformer.toEntity(request));
        return transformer.toResponse(entity);
    }

    @Transactional
    public E saveV2(RQ request) {
        return save(transformer.toEntity(request));
    }

    public RP find(ID id) {
        E entity = getOrElseThrow(id);
        return transformer.toResponse(entity);
    }

    @Transactional
    public void delete(RQ request) {
        delete(transformer.toEntity(request));
    }

}
