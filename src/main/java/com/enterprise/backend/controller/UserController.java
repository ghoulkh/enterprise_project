package com.enterprise.backend.controller;

import com.enterprise.backend.model.request.UserRequest;
import com.enterprise.backend.model.request.UpdateUserRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> registrationUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.registrationUser(request));
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfileByUsername(username));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }
}
