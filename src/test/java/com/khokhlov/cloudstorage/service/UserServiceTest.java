package com.khokhlov.cloudstorage.service;

import com.khokhlov.cloudstorage.exception.auth.UsernameAlreadyUsedException;
import com.khokhlov.cloudstorage.mapper.UserMapper;
import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.response.AuthResponse;
import com.khokhlov.cloudstorage.model.entity.User;
import com.khokhlov.cloudstorage.repository.UserRepository;
import com.khokhlov.cloudstorage.service.auth.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String TEST_USERNAME = "TestUser";
    private static final String RAW_PASSWORD = "testPassword";
    private static final String HASHED_PASSWORD = "encoded-hash";
    private static final AuthRequest TEST_AUTH_REQUEST = new AuthRequest(TEST_USERNAME, RAW_PASSWORD);

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void should_EncodesPasswordAndSaveUser_ForNewUsername() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.existsByUsernameIgnoreCase(TEST_USERNAME)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User(TEST_USERNAME, HASHED_PASSWORD));
        when(userMapper.toResponse(any(User.class))).thenReturn(new AuthResponse(TEST_USERNAME));

        AuthResponse response = userService.register(TEST_AUTH_REQUEST);

        verify(userRepository).existsByUsernameIgnoreCase(TEST_USERNAME);
        verify(passwordEncoder).encode(RAW_PASSWORD);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(saved.getPassword())
                .isNotEqualTo(RAW_PASSWORD)
                .isEqualTo(HASHED_PASSWORD);
        assertThat(response.username()).isEqualTo(TEST_USERNAME);
    }

    @Test
    void should_ThrowException_When_UsernameAlreadyUsed() {
        when(userRepository.existsByUsernameIgnoreCase(TEST_USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(TEST_AUTH_REQUEST))
                .isInstanceOf(UsernameAlreadyUsedException.class)
                .hasMessageContaining("Username already in use: ");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(userRepository).existsByUsernameIgnoreCase(TEST_USERNAME);
    }

}