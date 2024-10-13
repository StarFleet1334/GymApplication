package com.demo.folder.utils;

import com.demo.folder.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BlockUserFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockUserFilter.class);
    private final LoginAttemptService loginAttemptService;

    public BlockUserFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        LOGGER.info("doFilterInterval 1");
        if ("/api/login".equals(request.getRequestURI())) {
            LOGGER.info("doFilterInterval 2");
            if (username != null && loginAttemptService.isBlocked(username)) {
                LOGGER.info("doFilterInterval 3");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("User is blocked due to multiple failed login attempts.");
                return;
            }
        }
        LOGGER.info("doFilterInterval 4");

        filterChain.doFilter(request, response);
    }
}