package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.CodeForgotPass;
import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.service.repository.CodeForgotPassRepository;
import com.enterprise.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeForgotPassService {
    private final CodeForgotPassRepository codeForgotPassRepository;

    public CodeForgotPass generateCode(User user) {
        long currentTime = System.currentTimeMillis();
        Long expireTime = currentTime + 300000L;
        String code = Utils.getAlphaNumericString(10);
        CodeForgotPass codeForgotPass = CodeForgotPass.builder()
                .code(code)
                .expireDate(expireTime)
                .user(user).build();
        return codeForgotPassRepository.save(codeForgotPass);
    }

    public void validateCode(User user, String code) {
        CodeForgotPass codeForgotPass = codeForgotPassRepository.findByUserAndCode(user, code).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.CODE_INVALID));
        if (codeForgotPass.getExpireDate() < System.currentTimeMillis())
            throw new EnterpriseBackendException(ErrorCode.CODE_IS_EXPIRE);
        if (!code.equals(codeForgotPass.getCode()))
            throw new EnterpriseBackendException(ErrorCode.CODE_INVALID);
        codeForgotPassRepository.delete(codeForgotPass);
    }
}
