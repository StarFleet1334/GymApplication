package com.demo.folder.utils;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Generator {

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:',.<>?/~`";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {
        return IntStream.range(0, PASSWORD_LENGTH)
                .map(i -> RANDOM.nextInt(ALLOWED_CHARACTERS.length()))
                .mapToObj(ALLOWED_CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }


    public static String generateUserName(String firstName, String lastName) {
        return firstName + "." + lastName + "-" + UUID.randomUUID().toString().substring(0, 8);
    }




}