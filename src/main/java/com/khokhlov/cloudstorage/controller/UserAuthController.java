package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.controller.auth.AuthService;
import com.khokhlov.cloudstorage.model.dto.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.AuthResponse;
import com.khokhlov.cloudstorage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest req,
            HttpServletResponse resp) {

        var response = userService.register(request);
        authService.login(req, resp, request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest req,
            HttpServletResponse resp) {

        authService.login(req, resp, request.username(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(request.username()));
    }
}
