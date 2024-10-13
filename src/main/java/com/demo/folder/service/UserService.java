package com.demo.folder.service;

import com.demo.folder.entity.base.User;
import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.repository.UserRepository;
import com.demo.folder.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User authenticate(String username, String password) {
        LOGGER.info("Authenticating user with username: {}", username);
        User user = userRepository.findByUsernameWithAssociations(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            LOGGER.info("Authentication successful for username: {}", username);
            return user;
        }
        LOGGER.warn("Authentication failed for username: {}", username);
        throw new AuthenticationException("Invalid credentials");
    }


    @Transactional
    public void updatePassword(User user, String newPassword) {
        LOGGER.info("Updating password for user: {}", user.getUsername());
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }
        FileUtil.writeCredentialsToFile("trainee_credentials.txt", user.getUsername(), newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}