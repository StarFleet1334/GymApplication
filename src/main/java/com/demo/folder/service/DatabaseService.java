package com.demo.folder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

@Service
public class DatabaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public DatabaseService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = new DataSourceTransactionManager(dataSource);
    }


    public void resetAutoIncrement() {

        try {
            resetTable("TrainingSession");
            resetTable("User");
            resetTable("training_types");
            resetTable("trainings");
            resetTable("trainee_trainer");
            resetTable("trainees");
            resetTable("trainer_trainee");
            resetTable("trainers");
        } catch (Exception e) {
            LOGGER.error("Error resetting auto increment", e);
        }
    }

    private void resetTable(String tableName) {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
            jdbcTemplate.execute(String.format("DELETE FROM `%s`", tableName));
            jdbcTemplate.execute(String.format("ALTER TABLE `%s` AUTO_INCREMENT = 1", tableName));
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            System.err.println("Error resetting table: " + tableName + " due to " + e.getMessage());
        }
    }
}