package com.khokhlov.cloudstorage.config.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class CorrelationAndAccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        String cid = req.getHeader("X-Request-Id");
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        MDC.put("cid", cid);
        res.setHeader("X-Request-Id", cid);

        long start = System.currentTimeMillis();
        int status = 500;
        try {
            chain.doFilter(req, res);
            status = res.getStatus();
        } finally {
            long ms = System.currentTimeMillis() - start;
            String user = (req.getUserPrincipal() != null) ? req.getUserPrincipal().getName() : "anon";
            log.info("{} {} -> {} {} ({} ms)",
                    req.getMethod(), req.getRequestURI(), status, user, ms);
            MDC.remove("cid");
        }
    }
}
