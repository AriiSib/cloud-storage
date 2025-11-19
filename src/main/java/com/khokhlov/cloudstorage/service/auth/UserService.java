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
import org.springframework.dao.DataIntegrityViolationException;
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
        String username = request.username().trim();

        if (userRepository.existsByUsernameIgnoreCase(username))
            throw new UsernameAlreadyUsedException(username);

        try {
            return userMapper.toResponse(userRepository.save(
                    User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(request.password()))
                            .build()
            ));
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameAlreadyUsedException(username);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username.trim();
        User user = userRepository.findByUsernameIgnoreCase(normalized)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new CustomUserDetails(
                user.getId(),
                user.getPassword(),
                user.getUsername(),
                List.of(new SimpleGrantedAuthority(Role.USER.asAuthority()))
        );
    }
}
