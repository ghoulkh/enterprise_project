package com.enterprise.backend.service.base;

public interface BaseTransformer<M, RP , RQ> {
    M toEntity(RQ request);
    M responseToEntity(RP response);
    RP toResponse(M entity);
}
