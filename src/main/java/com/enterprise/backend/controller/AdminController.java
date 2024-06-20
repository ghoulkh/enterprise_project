package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.request.BanUserRequest;
import com.enterprise.backend.model.request.SetAuthorityRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.AdminService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Secured(AuthoritiesConstants.ROLE_SUPER_ADMIN)
    @GetMapping("/users")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<Page<UserResponse>> getUsersAndPaging(@RequestParam(name = "page_index", required = false, defaultValue = "1") int pageIndex,
                                                                @RequestParam(name = "page_size", required = false, defaultValue = "10") int pageSize,
                                                                @RequestParam(name = "keyword", required = false) String keyword,
                                                                @RequestParam(name = "type", required = false) String type) {
        return ResponseEntity.ok(adminService.getByType(pageIndex, pageSize, type, keyword));
    }

    @Secured(AuthoritiesConstants.ROLE_SUPER_ADMIN)
    @GetMapping("/users/search")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<List<UserResponse>> search(@RequestParam(name = "keyword") String keyword) {
        return ResponseEntity.ok(adminService.searchByEmailOrPhone(keyword));
    }

    @Secured(AuthoritiesConstants.ROLE_SUPER_ADMIN)
    @PostMapping("/authority")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<List<UserResponse>> setAuthority(@RequestBody @Valid SetAuthorityRequest request) {
        Authority.Role role = Authority.Role.valueOf(request.getRole());
        adminService.addAuthorityWithUserId(request.getUsername(), role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured(AuthoritiesConstants.ROLE_SUPER_ADMIN)
    @PostMapping("/ban")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<UserResponse> banUser(@RequestBody @Valid BanUserRequest request) {
        return ResponseEntity.ok(adminService.banUser(request.getUsername()));
    }
}
