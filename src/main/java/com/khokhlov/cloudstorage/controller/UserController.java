package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.config.security.CustomUserDetails;
import com.khokhlov.cloudstorage.model.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(new UserResponse(user.getUsername()));
    }

}
