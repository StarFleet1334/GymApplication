package com.demo.folder.controller.implementation;

import com.demo.folder.controller.skeleton.LoginControllerInterface;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.ChangeLoginRequestDTO;
import com.demo.folder.entity.dto.request.UserCredentials;
import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.service.JwtTokenBlacklistService;
import com.demo.folder.service.LoginAttemptService;
import com.demo.folder.service.UserService;
import com.demo.folder.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements LoginControllerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String INVALID_CREDENTIALS_MSG = "Invalid credentials";
    private static final String LOGIN_SUCCESS_MSG = "Login successful";
    private static final String LOGOUT_SUCCESS_MSG = "Logout successful";
    private static final String UPDATE_SUCCESS_MSG = "User password successfully updated.";
    private static final String NOT_LOGGED_IN_MSG = "You need to log in first.";

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @Override
    public ResponseEntity<String> login(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password,
            BindingResult result,
            HttpServletRequest request
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Objects.requireNonNull(result.getFieldError()).getDefaultMessage()
            );
        }
        HttpSession session = request.getSession(true);
        LOGGER.info("Session ID before authentication: {}", session.getId());

        try {
            authenticateUser(username, password, session);

            session = request.getSession(false);
            LOGGER.info("Session ID after authentication: {}", session.getId());

            loginAttemptService.loginSucceeded(username);
            session.setAttribute("USERNAME", username);
            LOGGER.info("Session Username after login: {}", session.getAttribute("USERNAME"));
            return ResponseEntity.ok(LOGIN_SUCCESS_MSG);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS_MSG);
        }
    }


    @Override
    public ResponseEntity<String> changeLogin(@Valid @RequestBody ChangeLoginRequestDTO changeLoginDTO,
                                              BindingResult result) {
        if (result.hasErrors()) {
            return getFailureResponse(result);
        }
        try {
            User user = userService.authenticate(changeLoginDTO.getUsername(), changeLoginDTO.getPassword());
            userService.updatePassword(user, changeLoginDTO.getNewPassword());
            LOGGER.info("Password updated for user: {}", user.getUsername());
            return ResponseEntity.ok(UPDATE_SUCCESS_MSG);
        } catch (AuthenticationException e) {
            LOGGER.warn("Invalid credentials for user: {}", changeLoginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password.");
        }
    }

    @Override
    public ResponseEntity<String> logout(@Valid @RequestBody UserCredentials credentials, HttpSession session) {
        String sessionUsername = (String) session.getAttribute("USERNAME");
        LOGGER.info("Session ID during logout: {}", session.getId());
        LOGGER.info("SESSION_USERNAME: {}", sessionUsername);
        LOGGER.info("CREDENTIALS_USERNAME: {}", credentials.getUsername());

        if (sessionUsername != null && sessionUsername.equals(credentials.getUsername())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName().equals(credentials.getUsername())) {
                String token = getTokenFromSession(session);
                if (token != null) {
                    LOGGER.info("TOKEN: {}", token);
                    blacklistToken(token, session);
                }
                logoutUser(session);
                return ResponseEntity.ok(LOGOUT_SUCCESS_MSG);
            } else {
                LOGGER.warn("No user authenticated in security context for logout.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_LOGGED_IN_MSG);
            }
        } else {
            LOGGER.warn("No matching session username found for logout.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_LOGGED_IN_MSG);
        }
    }

    private void authenticateUser(String username, String password, HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        setSessionAndSecurityContext(session, authentication);
    }

    private void setSessionAndSecurityContext(HttpSession session, Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        if (authentication != null) {
            session.setAttribute("USERNAME", authentication.getName());
        }
        LOGGER.info("Session ID during setup: {}", session.getId());
        LOGGER.info("Session Username during setup: {}", session.getAttribute("USERNAME"));
    }
    private String getTokenFromSession(HttpSession session) {
        Object tokenObj = session.getAttribute("TOKEN");
        return tokenObj != null ? tokenObj.toString() : null;
    }

    private void blacklistToken(String token, HttpSession session) {
        if (token != null) {
            jwtTokenBlacklistService.blacklistToken(token, jwtTokenUtil.getExpirationDateFromToken(token));
        }
        session.removeAttribute("USERNAME");
    }

    private void logoutUser(HttpSession session) {
        LOGGER.info("Invalidating session for user: {}", session.getAttribute("USERNAME"));
        SecurityContextHolder.clearContext();
        session.invalidate();
    }

    private ResponseEntity<String> getFailureResponse(BindingResult result) {
        String errorMessage = Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
        LOGGER.warn("Validation errors: {}", errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
    }
}