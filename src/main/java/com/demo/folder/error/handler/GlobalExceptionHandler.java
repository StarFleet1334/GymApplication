package com.demo.folder.error.handler;

import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.error.exception.ErrorResponse;
import com.demo.folder.mapper.RequiredRolesMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
                                                                       WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                       WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(
            org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Database error: " + Objects.requireNonNull(ex.getRootCause()).getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                        WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = (authentication != null && authentication.getName() != null)
                ? authentication.getName() : "anonymousUser";

        Collection<? extends GrantedAuthority> authorities = (authentication != null) ? authentication.getAuthorities() : null;
        String currentUserRoles = (authorities != null && !authorities.isEmpty())
                ? authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "))
                : "ROLE_ANONYMOUS";

        System.out.println("Authentication details: " + authentication);

        HttpServletRequest httpRequest = ((NativeWebRequest) request).getNativeRequest(HttpServletRequest.class);

        assert httpRequest != null;
        String requestUri = httpRequest.getRequestURI();
        System.out.println("Handling access denied exception for URI: " + requestUri);

        String requiredRole = RequiredRolesMapper.getRequiredRole(requestUri);
        System.out.println("Required role for URI " + requestUri + ": " + requiredRole);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access is denied: you do not have the required role(s) to access this resource.",
                request.getDescription(false)
        );
        errorResponse.setDetails(
                String.format("Current user '%s' with role(s) [%s] attempted to access. Required role(s): [%s]",
                        currentUser, currentUserRoles, requiredRole));

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}