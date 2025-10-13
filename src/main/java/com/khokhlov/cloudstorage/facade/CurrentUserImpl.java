package com.khokhlov.cloudstorage.facade;

import com.khokhlov.cloudstorage.model.entity.User;
import com.khokhlov.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserImpl implements CurrentUser {

    private final UserRepository userRepository;

    @Override
    public long getCurrentUserId() {
        UserDetails userDetails = getUserDetails();
        return userRepository.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("Authentication required");

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails)
            return userDetails;

        if (principal instanceof String string && !"anonymousUser".equalsIgnoreCase(string))
            return org.springframework.security.core.userdetails.User
                    .withUsername(string)
                    .password("")
                    .authorities(auth.getAuthorities())
                    .build();

        throw new AuthenticationCredentialsNotFoundException("Authentication required");
    }

}
