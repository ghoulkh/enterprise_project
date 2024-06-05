package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SetAuthorityRequest {
    @NotBlank(message = "username is required!")
    private String username;

    @NotBlank(message = "role is required!")
    @Pattern(regexp = "^(ROLE_ADMIN|ROLE_USER)$", message = "role value is ROLE_ADMIN or ROLE_USER")
    private String role;
}
