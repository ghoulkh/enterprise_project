package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.*;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.enums.OrderTypeStatus;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ProductOrderRequest;
import com.enterprise.backend.model.request.ProductRequest;
import com.enterprise.backend.model.request.SearchProductRequest;
import com.enterprise.backend.model.response.ProductResponse;
import com.enterprise.backend.security.SecurityUtil;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.*;
import com.enterprise.backend.service.transfomer.ProductOrderTransformer;
import com.enterprise.backend.service.transfomer.ProductTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        if (repo.countByTitle(productRequest.getTitle()) > 0) {
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

        if (!product.getTitle().equals(productRequest.getTitle())) {
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
        var result = transformer.toResponse(product);

        List<ProductResponse.OfCategory> ofCategories = new ArrayList<>();
        product.getCategories().forEach(category -> {
            var ofCategory = new ProductResponse.OfCategory();
            ofCategory.setId(category.getId());
            ofCategory.setName(category.getName());
            ofCategory.setPriority(category.getPriority());
            ofCategories.add(ofCategory);
        });

        result.setOfCategories(ofCategories);
        return result;
    }

    public List<ProductResponse> cacheListProducts(List<Long> productIds) {
        var products = repo.findAllById(productIds);
        return products.stream().map(transformer::toResponse).collect(Collectors.toList());
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

        if (StringUtils.isNotEmpty(searchRequest.getTitle())) {
            query.where(qProduct.title.containsIgnoreCase(searchRequest.getTitle()));
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
        AtomicReference<Integer> quantity = new AtomicReference<>(0);
        AtomicReference<Long> price = new AtomicReference<>(0L);

        request.getProducts().forEach(orderRequest -> {
            Product product = getOrElseThrow(orderRequest.getProductId());
            if (product.getQuantity() < orderRequest.getQuantity()) {
                throw new EnterpriseBackendException(ErrorCode.QUANTITY_WRONG);
            }
            product.setQuantity(product.getQuantity() - orderRequest.getQuantity());
            repo.save(product);

            Orders order = Orders.builder()
                    .product(product)
                    .quantity(orderRequest.getQuantity())
                    .build();
            orders.add(orderRepository.save(order));
            quantity.updateAndGet(v -> v + orderRequest.getQuantity());
            price.updateAndGet(v -> v + (orderRequest.getQuantity() * product.getPrice()));
        });
        ProductOrder productOrder = productOrderTransformer.toEntity(request);
        productOrder.setQuantity(quantity.get());
        productOrder.setPrice(price.get());
        productOrder.setStatus(OrderStatus.NEW);
        productOrder.setType(OrderTypeStatus.BUY);

        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            userId = request.getUserId();
        }
        if (StringUtils.isNotEmpty(userId)) {
            userRepository.findById(userId).ifPresent(productOrder::setUser);
        }
        productOrder.setOrders(orders);
        productOrderRepository.save(productOrder);

        orders.forEach(order -> {
            order.setProductOrder(productOrder);
            orderRepository.save(order);
        });

        new Thread(() -> emailService.sendNewOrder(request.getHtmlContent(), request.getEmail())).start();
    }

    @Transactional
    public void addFavoriteProduct(Long productId) {
        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        Product product = getOrElseThrow(productId);

        Orders order = Orders.builder()
                .product(product)
                .build();

        Set<Orders> orders = new HashSet<>();
        orders.add(orderRepository.save(order));
        ProductOrder productOrder = ProductOrder.builder()
                .orders(orders)
                .email(user.getEmail())
                .note("Favorite product!")
                .type(OrderTypeStatus.FAVORITE)
                .addressDetail(user.getAddress())
                .phoneNumber(user.getPhone())
                .receiverFullName(user.getFullName())
                .user(user)
                .build();

        productOrderRepository.save(productOrder);

        order.setProductOrder(productOrder);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteProductFavorite(Long productOrderId) {
        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        var productOrder = productOrderRepository.findByUserAndIdAndType(user, productOrderId, OrderTypeStatus.FAVORITE)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.PRODUCT_ORDER_NOT_FOUND));

        productOrder.setType(OrderTypeStatus.FAVORITE_HISTORY);
        productOrderRepository.save(productOrder);
    }

    @Transactional
    public void updateOrderStatus(Long productOrderId, OrderStatus status) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.ORDER_NOT_FOUND));
        switch (status) {
            case CANCELLED:
                productOrder.getOrders().forEach(order -> {
                    var product = order.getProduct();
                    product.setQuantity(product.getQuantity() + order.getQuantity());
                    repo.save(product);
                });
                break;
            case NEW:
            case PENDING:
            case COMPLETED:
            default:
                break;
        }
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

    public String uploadFiles(HttpServletRequest request,
                              MultipartFile file) {
        if (file != null) {
            try {
                File imageDir = new File("./image");

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    log.info("File originalName not found!");
                    return null;
                }

                if (!imageDir.exists()) {
                    log.info("Create imageDir: {} - {}", file.getOriginalFilename(), imageDir.mkdirs());
                }

                String fileName = org.springframework.util.StringUtils.cleanPath(originalFilename);
                Path path = Paths.get(imageDir.getAbsolutePath() + File.separator + "product_" + fileName);
                int i = 0;
                while (path.toFile().exists()) {
                    path = Paths.get(imageDir.getAbsolutePath() + File.separator + "product_" + i + "_" + fileName);
                    i++;
                }
                Files.write(path, file.getBytes());

                String domain = request.getScheme() + "://" + request.getServerName() + "/api/product";

                return domain + "/images/" + path.getFileName();
            } catch (IOException e) {
                log.error("Error uploading file: {}", e.getMessage(), e);
            }
        }
        return null;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            Path filePath = Paths.get("image").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(Files.probeContentType(filePath) != null ?
                                Files.probeContentType(filePath) : "application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            log.error("Error getting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            log.error("Error determining file type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    protected String notFoundMessage() {
        return "Not found product";
    }
}
