package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.Optional;

public interface AuthorityRepository extends BaseCommonRepository<Authority, Long> {
    Optional<Authority> findByUserAndRole(User user, Authority.Role role);
}
