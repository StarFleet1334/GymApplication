package com.demo.folder.mapper;

import java.util.HashMap;
import java.util.Map;

public class RequiredRolesMapper {

    private static final Map<String, String> urlRoleMap = new HashMap<>();

    static {
        urlRoleMap.put("/api/training-type", "ROLE_ADMIN, ROLE_TRAINER");
        urlRoleMap.put("/api/trainings", "ROLE_ADMIN");
        urlRoleMap.put("/api/trainers", "ROLE_ADMIN, ROLE_TRAINER");
        urlRoleMap.put("/api/trainees", "ROLE_ADMIN, ROLE_TRAINEE");
    }

    public static String getRequiredRole(String url) {
        System.out.println("Requested URL: " + url);

        for (Map.Entry<String, String> entry : urlRoleMap.entrySet()) {
            String urlPattern = entry.getKey();
            String regexPattern = urlPattern.replace("**", ".*");
            System.out.println("Checking pattern: " + regexPattern + " against URL: " + url);

            if (url.matches(regexPattern)) {
                return entry.getValue();
            }
        }
        return "Unknown role";
    }
}