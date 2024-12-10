package com.demo.folder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class DatabaseInitializer {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void init() {
        System.out.println("DatabaseInitializer starting...");
        databaseService.resetAutoIncrement();
        System.out.println("DatabaseInitializer completed.");
    }
}
