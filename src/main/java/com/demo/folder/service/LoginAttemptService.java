package com.demo.folder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = TimeUnit.MINUTES.toMillis(5);

    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockCache.remove(username);
    }

    public void clearAttempts(String username) {
        attemptsCache.remove(username);
        lockCache.remove(username);
        LOGGER.info("Cleared login attempts and locks for user {}", username);
    }

    public void loginFailed(String username) {
        int currentAttempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, currentAttempts);

        if (currentAttempts >= MAX_ATTEMPTS) {
            long currentTimeMillis = System.currentTimeMillis();
            lockCache.put(username, currentTimeMillis);
            logUserStatus(username, currentTimeMillis, "locked out at");
        }

        LOGGER.info("User {} login attempts: {}", username, currentAttempts);
    }

    public boolean isBlocked(String username) {
        Long lockTimestamp = lockCache.get(username);

        if (lockTimestamp != null) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - lockTimestamp;

            logTimeSinceLock(username, elapsedTimeMillis);

            if (elapsedTimeMillis > LOCK_TIME_DURATION) {
                logUserStatus(username, currentTimeMillis, "block expired at");
                lockCache.remove(username);
                attemptsCache.remove(username);
                return false;
            }

            LOGGER.info("User {} is still blocked.", username);
            return true;
        }

        LOGGER.info("No lock timestamp found for user {}, not currently blocked.", username);
        return false;
    }

    private void logTimeSinceLock(String username, long elapsedTimeMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) - TimeUnit.MINUTES.toSeconds(minutes);
        LOGGER.info("Checking block for user {}, time since lock: {} minutes, {} seconds", username, minutes, seconds);
    }

    private void logUserStatus(String username, long currentTimeMillis, String status) {
        LOGGER.info("User {} {} {}", username, status, currentTimeMillis);
    }
}