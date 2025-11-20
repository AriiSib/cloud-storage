package com.khokhlov.cloudstorage.integration.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khokhlov.cloudstorage.exception.auth.UsernameAlreadyUsedException;
import com.khokhlov.cloudstorage.integration.service.IntegrationTestBase;
import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(statements = {
        "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor
class UserAuthControllerIT extends IntegrationTestBase {
    private static final String TEST_USERNAME = "TestUser";
    private static final String RAW_PASSWORD = "testPassword";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    void should_CreateSession_And_ReturnResponseUsernameOnly() throws Exception {
        var body = objectMapper.writeValueAsString(new AuthRequest(TEST_USERNAME, RAW_PASSWORD));

        MvcResult signUp = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", aMapWithSize(1)))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andReturn();

        var sessionCookie = signUp.getResponse().getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();

        mockMvc.perform(get("/api/user/me").cookie(sessionCookie))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(1)))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));
    }

    @Test
    void should_ThrowException_When_UserIsAlreadyExists() throws Exception {
        var body = objectMapper.writeValueAsString(new AuthRequest(TEST_USERNAME, RAW_PASSWORD));

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", aMapWithSize(1)))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));

        MvcResult failSignUp = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(UsernameAlreadyUsedException.class))
                .andExpect(jsonPath("$.message").value(startsWith("Username already in use: ")))
                .andReturn();

        MockHttpSession session = (MockHttpSession) failSignUp.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @Test
    void should_Login_When_UserIsExistAndValid() throws Exception {
        var body = objectMapper.writeValueAsString(new AuthRequest(TEST_USERNAME, RAW_PASSWORD));

        MvcResult signUp = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", aMapWithSize(1)))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andReturn();

        var sessionCookie = signUp.getResponse().getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();

        MvcResult login = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andReturn();

        var loginCookie = login.getResponse().getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();
        assertThat(sessionCookie).isNotEqualTo(loginCookie); //if ChangeSessionIdAuthenticationStrategy
    }

    @ParameterizedTest
    @MethodSource("com.khokhlov.cloudstorage.integration.controller.auth.UserAuthControllerIT#getArgumentsForLoginTest")
    void should_ThrowException_When_InvalidLogin(String username, String password) throws Exception {
        var body = objectMapper.writeValueAsString(new AuthRequest(username, password));

        MvcResult login = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class))
                .andReturn();
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("T", RAW_PASSWORD),
                Arguments.of(TEST_USERNAME, "123"),
                Arguments.of("1", "1"),
                Arguments.of("", ""),
                Arguments.of(null, RAW_PASSWORD),
                Arguments.of(TEST_USERNAME, null),
                Arguments.of(null, null));
    }
}
