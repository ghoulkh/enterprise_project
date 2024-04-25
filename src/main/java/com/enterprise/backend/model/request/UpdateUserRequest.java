package com.enterprise.backend.model.request;

import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.util.Utils;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UpdateUserRequest {
    private String fullName;
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "invalid email!")
    private String email;
    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "invalid phone number!")
    private String phone;
    private Boolean isActive;
    private String avaUrl;
    private String address;

    public void updateUser(User user) {
        Utils.copyPropertiesNotNull(this, user);
        if (isActive != null)
            user.setActive(this.isActive);
    }
}
