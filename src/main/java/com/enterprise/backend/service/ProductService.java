package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.*;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ProductOrderRequest;
import com.enterprise.backend.model.request.ProductRequest;
import com.enterprise.backend.model.request.SearchProductRequest;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.security.SecurityUtil;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.*;

import java.util.*;
import java.util.stream.Collectors;

import com.enterprise.backend.service.transfomer.ProductOrderTransformer;
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

    private static final QProduct qProduct = QProduct.product;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();

    private final EmailService emailService;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderTransformer productOrderTransformer;
    private final UserRepository userRepository;

    protected ProductService(ProductRepository repo,
                             ProductTransformer transformer,
                             EntityManager em,
                             EmailService emailService,
                             ProductOrderTransformer productOrderTransformer,
                             CategoryRepository categoryRepository,
                             OrderRepository orderRepository,
                             ProductOrderRepository productOrderRepository,
                             UserRepository userRepository) {
        super(repo, transformer, em);
        this.emailService = emailService;
        this.productOrderTransformer = productOrderTransformer;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        sortProperties.put(Product.Fields.id, qProduct.id);
        sortProperties.put(Product.Fields.price, qProduct.price);
        sortProperties.put(Product.Fields.rate, qProduct.rate);
        sortProperties.put(Auditable.Fields.createdDate, qProduct.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qProduct.updatedDate);
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
                .map(transformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, responses.size());
    }

    private JPAQuery<Product> searchQuery(SearchProductRequest searchRequest) {
        JPAQuery<Product> query = queryFactory.selectFrom(qProduct);

        if (ObjectUtils.isNotEmpty(searchRequest.getCategoryId())) {
            QCategory qCategory = QCategory.category;
            query.innerJoin(qProduct.categories, qCategory)
                    .where(qCategory.id.eq(searchRequest.getCategoryId()));
        }

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

        if (searchRequest.getFromModifiedDate() != null) {
            query.where(qProduct.updatedDate.goe(searchRequest.getFromModifiedDate()));
        }

        if (searchRequest.getToModifiedDate() != null) {
            query.where(qProduct.updatedDate.loe(searchRequest.getToModifiedDate()));
        }

        queryPage(searchRequest, query);
        sortBy(searchRequest.getOrderBy(), searchRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    @Transactional
    public void orderProduct(ProductOrderRequest request) {
        Set<Orders> orders = new HashSet<>();
        request.getProducts().forEach(orderRequest -> {
            Product product = getOrElseThrow(orderRequest.getProductId());
            Orders order = Orders.builder()
                    .product(product)
                    .quantity(orderRequest.getQuantity())
                    .build();
            orders.add(orderRepository.save(order));
        });
        ProductOrder productOrder = productOrderTransformer.toEntity(request);
        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            userId = request.getUserId();
        }
        if (StringUtils.isNotEmpty(userId)) {
            userRepository.findById(userId).ifPresent(productOrder::setUser);
        }
        productOrder.setOrders(orders);
        productOrderRepository.save(productOrder);
        new Thread(() -> emailService.sendNewOrder(request.getHtmlContent(), request.getEmail())).start();
    }

    public void updateOrderStatus(Long productOrderId, OrderStatus status) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.ORDER_NOT_FOUND));
        productOrderRepository.updateProductOrderByStatus(productOrder.getId(), status);

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
