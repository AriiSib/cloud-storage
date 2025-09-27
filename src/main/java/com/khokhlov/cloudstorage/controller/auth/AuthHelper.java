package com.khokhlov.cloudstorage.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private final AuthenticationManager authManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionStrategy;

    public void login(HttpServletRequest request, HttpServletResponse response,
                      String username, String rawPassword) {

        var authReq = UsernamePasswordAuthenticationToken.unauthenticated(username, rawPassword);
        Authentication auth = authManager.authenticate(authReq);

        // change the ID session (for session-fixation attacks)
        sessionStrategy.onAuthentication(auth, request, response);

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        securityContextRepository.saveContext(context, request, response);
    }
}
