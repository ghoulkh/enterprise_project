package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Orders;
import com.enterprise.backend.model.entity.ProductOrder;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends BaseCommonRepository<Orders, Long> {
    @Query(value = "SELECT o FROM ProductOrder o")
    List<ProductOrder> getAll(Pageable pageable);
}
