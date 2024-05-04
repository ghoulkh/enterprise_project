package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.CodeForgotPass;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.List;
import java.util.Optional;

public interface CodeForgotPassRepository extends BaseCommonRepository<CodeForgotPass, Long> {
    Optional<CodeForgotPass> findByUserAndCode(User user, String code);

    Optional<List<CodeForgotPass>> findAllByUser(User user);
}
