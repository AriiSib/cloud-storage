package com.khokhlov.cloudstorage.service.auth;

import com.khokhlov.cloudstorage.exception.auth.UsernameAlreadyUsedException;
import com.khokhlov.cloudstorage.mapper.UserMapper;
import com.khokhlov.cloudstorage.config.security.CustomUserDetails;
import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.response.AuthResponse;
import com.khokhlov.cloudstorage.model.entity.Role;
import com.khokhlov.cloudstorage.model.entity.User;
import com.khokhlov.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthResponse register(AuthRequest request) {
        User user = userMapper.toNewUser(request);

        if (userRepository.existsByUsernameIgnoreCase(user.getUsername()))
            throw new UsernameAlreadyUsedException(user.getUsername());

        user.setPassword(passwordEncoder.encode(request.password()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new CustomUserDetails(
                user.getId(),
                user.getPassword(),
                user.getUsername(),
                List.of(new SimpleGrantedAuthority(Role.USER.asAuthority()))
        );
    }
}
