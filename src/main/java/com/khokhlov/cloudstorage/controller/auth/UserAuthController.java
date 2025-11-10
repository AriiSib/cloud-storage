package com.khokhlov.cloudstorage.controller.auth;

import com.khokhlov.cloudstorage.docs.auth.LoginDocs;
import com.khokhlov.cloudstorage.docs.auth.LogoutDocs;
import com.khokhlov.cloudstorage.docs.auth.RegisterDocs;
import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.response.AuthResponse;
import com.khokhlov.cloudstorage.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1. Authentication", description = "User authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserService userService;
    private final AuthService authService;

    @RegisterDocs
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest req,
            HttpServletResponse resp) {

        var response = userService.register(request);
        authService.login(req, resp, request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @LoginDocs
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest req,
            HttpServletResponse resp) {

        authService.login(req, resp, request.username(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(request.username()));
    }

    @LogoutDocs
    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse resp) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(req, resp, auth);
        return ResponseEntity.noContent().build();
    }
}
