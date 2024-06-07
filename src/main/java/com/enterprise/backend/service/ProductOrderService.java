package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.*;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ProductOrderRequest;
import com.enterprise.backend.model.request.SearchProductOrderRequest;
import com.enterprise.backend.model.response.ProductOrderResponse;
import com.enterprise.backend.security.SecurityUtil;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.ProductOrderRepository;
import com.enterprise.backend.service.repository.UserRepository;
import com.enterprise.backend.service.transfomer.ProductOrderTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
public class ProductOrderService extends BaseService<ProductOrder, Long, ProductOrderRepository, ProductOrderTransformer, ProductOrderRequest, ProductOrderResponse> {

    private static final QProductOrder qProductOrder = QProductOrder.productOrder;
    private final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();

    private final UserRepository userRepository;

    protected ProductOrderService(ProductOrderRepository repo,
                                  ProductOrderTransformer transformer,
                                  EntityManager em,
                                  UserRepository userRepository) {
        super(repo, transformer, em);
        this.userRepository = userRepository;
        sortProperties.put(Product.Fields.id, qProductOrder.id);
        sortProperties.put(Auditable.Fields.createdDate, qProductOrder.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qProductOrder.updatedDate);
    }

    public Page<ProductOrderResponse> getProductOrderByMe(SearchProductOrderRequest searchRequest) {
        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(searchRequest.getPageNumber(), searchRequest.getPageSize());
        JPAQuery<ProductOrder> search = createProductOrderQuery(searchRequest, user.getId());
        log.info("Search product order by me query: {}", search);

        List<ProductOrderResponse> responses = search.fetch()
                .stream()
                .map(transformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageRequest, responses.size());
    }

    public Page<ProductOrderResponse> adminSearchProductOrder(SearchProductOrderRequest searchRequest, String userId) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getPageNumber(), searchRequest.getPageSize());
        JPAQuery<ProductOrder> search = createProductOrderQuery(searchRequest, userId);
        log.info("Admin search product order query: {}", search);

        List<ProductOrderResponse> responses = search.fetch()
                .stream()
                .map(transformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageRequest, responses.size());
    }

    private JPAQuery<ProductOrder> createProductOrderQuery(SearchProductOrderRequest searchRequest, String userId) {
        JPAQuery<ProductOrder> query = queryFactory.selectFrom(qProductOrder);

        if (StringUtils.isNotEmpty(userId)) {
            query.innerJoin(qProductOrder.user, QUser.user)
                    .where(QUser.user.id.eq(userId));
        }

        if (StringUtils.isNotEmpty(searchRequest.getPhoneNumber())) {
            query.where(qProductOrder.phoneNumber.containsIgnoreCase(searchRequest.getPhoneNumber()));
        }

        if (StringUtils.isNotEmpty(searchRequest.getEmail())) {
            query.where(qProductOrder.email.containsIgnoreCase(searchRequest.getEmail()));
        }

        if (StringUtils.isNotEmpty(searchRequest.getReceiverFullName())) {
            query.where(qProductOrder.receiverFullName.containsIgnoreCase(searchRequest.getReceiverFullName()));
        }

        if (StringUtils.isNotEmpty(searchRequest.getAddressDetail())) {
            query.where(qProductOrder.addressDetail.containsIgnoreCase(searchRequest.getAddressDetail()));
        }

        if (ObjectUtils.isNotEmpty(searchRequest.getStatus())) {
            query.where(qProductOrder.status.eq(searchRequest.getStatus()));
        }

        if (ObjectUtils.isNotEmpty(searchRequest.getType())) {
            query.where(qProductOrder.type.eq(searchRequest.getType()));
        }

        if (searchRequest.getToCreatedDate() != null) {
            query.where(qProductOrder.createdDate.loe(searchRequest.getToCreatedDate()));
        }

        if (searchRequest.getFromModifiedDate() != null) {
            query.where(qProductOrder.updatedDate.goe(searchRequest.getFromModifiedDate()));
        }

        if (searchRequest.getFromCreatedDate() != null) {
            query.where(qProductOrder.createdDate.goe(searchRequest.getFromCreatedDate()));
        }

        if (searchRequest.getToModifiedDate() != null) {
            query.where(qProductOrder.updatedDate.loe(searchRequest.getToModifiedDate()));
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