package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.Review;
import com.enterprise.backend.model.request.ReviewRequest;
import com.enterprise.backend.model.response.ReviewResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ReviewTransformer extends BaseTransformer<Review, ReviewResponse, ReviewRequest> {
    @AfterMapping
    default void afterMappingToResponse(Review entity, @MappingTarget ReviewResponse response) {
        if (ObjectUtils.isNotEmpty(entity)
                && StringUtils.isNotEmpty(entity.getUser().getFullName())) {
            response.setFullName(entity.getUser().getFullName());
        }

        if (ObjectUtils.isNotEmpty(entity)
                && StringUtils.isNotEmpty(entity.getUser().getId())) {
            response.setUserId(entity.getUser().getId());
        }
    }
}
