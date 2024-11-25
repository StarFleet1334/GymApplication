package com.demo.folder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenBlacklistService {


    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenBlacklistService.class);
    private final Map<String, Date> blacklistedTokens = new HashMap<>();

    public void blacklistToken(String token, Date expirationDate) {
        blacklistedTokens.put(token, expirationDate);
        LOGGER.info("BlackListed tokens: {}",blacklistedTokens);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
}