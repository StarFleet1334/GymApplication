package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public CustomUserDetailsService(TraineeService traineeService, TrainerService trainerService,
                                    LoginAttemptService loginAttemptService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("LoadUserByUsername called with username: {}", username);

        if (username == null || username.isEmpty()) {
            LOGGER.error("Username is null or empty");
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }
        if (loginAttemptService.isBlocked(username)) {
            LOGGER.warn("User is blocked due to multiple failed login attempts: {}", username);
            throw new UsernameNotFoundException("User is temporarily blocked. Please try again later.");
        }

        Trainer trainer = trainerService.findTrainerByUsername(username);
        Trainee trainee = traineeService.findTraineeByUsername(username);

        if (trainee == null && trainer == null) {
            LOGGER.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        if (trainee != null) {
            LOGGER.info("Trainee found, returning UserDetails for trainee: {}", trainee.getUser().getUsername());
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE"));
            return new User(trainee.getUser().getUsername(), trainee.getUser().getPassword(), authorities);
        }

        LOGGER.info("Trainer found, returning UserDetails for trainer: {}", trainer.getUser().getUsername());
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINER"));
        return new User(trainer.getUser().getUsername(), trainer.getUser().getPassword(), authorities);
    }
}