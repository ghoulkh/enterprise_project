package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.ProductOrder;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.enums.OrderTypeStatus;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductOrderRepository extends BaseCommonRepository<ProductOrder, Long> {
    @Modifying
    @Query("UPDATE ProductOrder e SET e.status = :status WHERE e.id = :id")
    void updateProductOrderByStatus(Long id, OrderStatus status);

    Page<ProductOrder> findAllByUser(User user, Pageable pageable);

    Optional<ProductOrder> findByUserAndIdAndType(User user, Long id, OrderTypeStatus type);
}
