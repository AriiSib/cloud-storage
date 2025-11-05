package com.khokhlov.cloudstorage.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionStrategy;

    public void login(HttpServletRequest request, HttpServletResponse response,
                      String username, String rawPassword) {

        Authentication authResult = authManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, rawPassword)
        );

        // if there is no session, then will not work sessionStrategy.onAuthentication()
        request.getSession(true);
        // change the ID session (for session-fixation attacks)
        sessionStrategy.onAuthentication(authResult, request, response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
