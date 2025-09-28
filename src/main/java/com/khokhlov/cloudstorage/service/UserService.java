package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.exception.UsernameAlreadyUsedException;
import com.khokhlov.cloudstorage.mapper.UserMapper;
import com.khokhlov.cloudstorage.model.dto.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.AuthResponse;
import com.khokhlov.cloudstorage.model.entity.User;
import com.khokhlov.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String username = request.username().trim().toLowerCase();
        if (userRepository.existsByUsername(username))
            throw new UsernameAlreadyUsedException(username);

        User user = userMapper.fromRequest(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username.trim().toLowerCase();
        var user = userRepository.findByUsername(normalized)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
