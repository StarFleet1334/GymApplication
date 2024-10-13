package com.demo.folder.config;

import com.demo.folder.service.JwtTokenBlacklistService;
import com.demo.folder.utils.JwtTokenUtil;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;

    @Value("${hardcoded.admin.username:admin}")
    private String adminUsername;

    @Value("${hardcoded.admin.password:admin}")
    private String adminPassword;



    @Autowired
    public CustomAuthenticationProvider(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, JwtTokenBlacklistService jwtTokenBlacklistService) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        try {
            final var user = userDetailsService.loadUserByUsername(username);

            if (isTokenValid(password, username)) {
                return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
            }

            if (user != null && user.getPassword().equals(password)) {
                return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid credentials", e);
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("Invalid token", e);
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    private boolean isTokenValid(String token, String username) {
        if (jwtTokenUtil.isWellFormedToken(token) && !jwtTokenBlacklistService.isTokenBlacklisted(token)) {
            return jwtTokenUtil.validateToken(token, username);
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}