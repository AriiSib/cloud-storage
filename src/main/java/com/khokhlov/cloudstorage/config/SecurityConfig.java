package com.khokhlov.cloudstorage.config;

import com.khokhlov.cloudstorage.config.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilter(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/config.js",
                                "/assets/**",
                                "/login",
                                "/registration",
                                "/files/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/sign-up", "/api/auth/sign-in").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilter(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .invalidSessionStrategy(new SimpleRedirectInvalidSessionStrategy("/login"))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )

                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/config.js",
                                "/assets/**",
                                "/login",
                                "/registration",
                                "/files/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/sign-up", "/api/auth/sign-in").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION")
                )
                .build();
    }

//    @Bean
//    public RedisOperations<String, Object> sessionRedisOperations() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//
//    @Bean
//    public FindByIndexNameSessionRepository<? extends Session> redisSessionRepository(RedisOperations<String, Object> sessionRedisOperations) {
//        return new RedisIndexedSessionRepository(sessionRedisOperations);
//    }
//
//    @Bean
//    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(
//            FindByIndexNameSessionRepository<? extends Session> sessionsRepository) {
//        return new SpringSessionBackedSessionRegistry<>(sessionsRepository);
//    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // Change the ID session with the login (protection against fixing sessions) and set max sessions
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
    }
//    @Bean
//    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
//            SpringSessionBackedSessionRegistry<?> sessionRegistry) {
//
//        var concurrent = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
//        concurrent.setMaximumSessions(1);
//        concurrent.setExceptionIfMaximumExceeded(true);
//
//        var changeId = new ChangeSessionIdAuthenticationStrategy();
//        var register = new RegisterSessionAuthenticationStrategy(sessionRegistry);
//
//        return new CompositeSessionAuthenticationStrategy(List.of(concurrent, changeId, register));
//    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder pe) {
        var provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(pe);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
