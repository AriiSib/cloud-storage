package com.khokhlov.cloudstorage.facade;

import com.khokhlov.cloudstorage.model.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/*
    Alternative for @AuthenticationPrincipal(expression = "id") long userId
*/

//@Component
//@RequiredArgsConstructor
//public class CurrentUserImpl implements CurrentUser {
//
//    @Override
//    public long getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
//            throw new AuthenticationCredentialsNotFoundException("Authentication required");
//
//        Object principal = auth.getPrincipal();
//        if (principal instanceof CustomUserDetails customUserDetails)
//            return customUserDetails.getId();
//
//        throw new AuthenticationCredentialsNotFoundException("Unexpected principal type");
//    }
//
//}