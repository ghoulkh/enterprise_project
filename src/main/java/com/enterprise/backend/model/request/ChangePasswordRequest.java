package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "username is required!")
    private String username;
    @NotBlank(message = "oldPassword is required!")
    private String oldPassword;
    @NotBlank(message = "newPassword is required!")
    private String newPassword;
}

