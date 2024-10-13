package com.demo.folder.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = TransactionIdHolder.getTransactionId();
        LOGGER.info("Transaction ID: {} | Incoming request: {} {}", transactionId, request.getMethod(),
                request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        String transactionId = TransactionIdHolder.getTransactionId();
        LOGGER.info("Transaction ID: {} | Response status: {}", transactionId, response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String transactionId = TransactionIdHolder.getTransactionId();
        if (ex != null) {
            LOGGER.error("Transaction ID: {} | Error occurred: {}", transactionId, ex.getMessage());
        }
    }
}