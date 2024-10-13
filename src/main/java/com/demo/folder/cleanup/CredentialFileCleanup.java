package com.demo.folder.cleanup;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CredentialFileCleanup {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialFileCleanup.class);
    private final Path traineeCredentialsPath = Paths.get("trainee_credentials.txt");
    private final Path trainerCredentialsPath = Paths.get("trainer_credentials.txt");

    @PreDestroy
    public void cleanUp() {
        deleteFile(traineeCredentialsPath);
        deleteFile(trainerCredentialsPath);
    }

    private void deleteFile(Path path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.info("Deleted file: {}", path.toAbsolutePath());
            } else {
                LOGGER.warn("File not found, cannot delete: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Error deleting file {}: {}", path.toAbsolutePath(), e.getMessage());
        }
    }
}