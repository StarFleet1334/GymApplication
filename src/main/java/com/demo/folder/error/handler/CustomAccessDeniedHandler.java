package com.demo.folder.error.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = (authentication != null && authentication.getName() != null) ? authentication.getName() : "unauthenticated user";

        Collection<? extends GrantedAuthority> authorities = (authentication != null) ? authentication.getAuthorities() : null;
        String currentUserRoles = (authorities != null) ? authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")) : "No roles";

        String requestUri = request.getRequestURI();

        LOGGER.warn("User '{}' with roles [{}] attempted to access the protected URL: {}",
                currentUser, currentUserRoles, requestUri);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(String.format("User '%s' with roles [%s] attempted to access URL '%s'. Access is denied.",
                currentUser, currentUserRoles, requestUri));
    }
}