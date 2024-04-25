package com.enterprise.backend.service.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface BaseCommonRepository<E, ID> extends JpaRepository<E, ID>, PagingAndSortingRepository<E, ID>, JpaSpecificationExecutor<E> {

    List<E> findByIdIn(List<ID> ids);
}