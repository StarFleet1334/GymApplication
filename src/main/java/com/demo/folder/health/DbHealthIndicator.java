package com.demo.folder.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        return checkDatabase();
    }

    private Health checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                List<String> tables = getTableNames();
                return Health.up()
                        .withDetail("Database Status", "Up")
                        .withDetail("Table Count", tables.size())
                        .withDetail("Tables", tables)
                        .build();
            } else {
                return Health.down()
                        .withDetail("Database Status", "Down - Connection is not valid")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down(e)
                    .withDetail("Database Status", "Down - Error while getting database connection")
                    .build();
        }
    }

    private List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'";
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }
}