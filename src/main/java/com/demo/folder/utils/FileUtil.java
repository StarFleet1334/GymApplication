package com.demo.folder.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void writeCredentialsToFile(String filePath, String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("--------------------------------------------------");
            writer.newLine();
            writer.write(String.format("| Username: %-40s |", username));
            writer.newLine();
            writer.write(String.format("| Password: %-40s |", password));
            writer.newLine();
            writer.write("--------------------------------------------------");
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}