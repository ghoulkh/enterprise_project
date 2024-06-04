package com.enterprise.backend.model.response;

import com.enterprise.backend.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserResponse {
    private String id;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date lastModifiedDate;
    private List<AuthorityResponse> authorities;
    private String fullName;
    private String avaUrl;
    private String email;
    private String createdBy;
    private String phone;
    private Double ratingValue;
    private String address;
    private boolean isActive;

    public static UserResponse from(User user) {
        UserResponse result = new UserResponse();
        BeanUtils.copyProperties(user, result);
        if (user.getAuthorities() != null)
            result.setAuthorities(user.getAuthorities().stream()
                    .map(AuthorityResponse::from)
                    .collect(Collectors.toList()));
        return result;
    }
}
