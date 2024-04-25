package com.enterprise.backend.model.request;

import com.enterprise.backend.model.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
public class UserRequest {
    @NotBlank(message = "password is required!")
    private String password;
    @NotBlank(message = "fullName is required!")
    private String fullName;
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "invalid email!")
    @NotBlank(message = "email is required!")
    private String email;
    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "invalid phone number!")
    @NotBlank(message = "phone is required!")
    private String phone;
    private String avaUrl;
    private String address;

    public User toUser() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setFullName(this.fullName);
        user.setActive(true);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setAvaUrl(this.avaUrl);
        user.setProvider(User.Provider.LOCAL);
        user.setAddress(this.address);
        return user;
    }
}

