package com.bridgetrack.bridgetrack.config;

import java.text.Normalizer;

public final class StudentPasswordGenerator {
    private StudentPasswordGenerator() {}

    // Andon + Langley -> andonlangley!
    public static String fromFirstLast(String firstName, String lastName) {
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";

        String base = (firstName + lastName).toLowerCase();

        // normalize accents (José -> jose)
        base = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // remove spaces, hyphens, apostrophes, etc.
        base = base.replaceAll("[^a-z0-9]", "");

        if (base.isBlank()) {
            base = "student";
        }

        return base + "!";
    }

    public static String fromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "TempPassword123!";
        }

        String localPart = email.split("[@#]")[0];
        String[] words = localPart.split("\\.");
        StringBuilder password = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                StringBuilder letters = new StringBuilder();
                StringBuilder numbers = new StringBuilder();

                for (char c : word.toCharArray()) {
                    if (Character.isLetter(c)) letters.append(c);
                    else if (Character.isDigit(c)) numbers.append(c);
                }

                if (letters.length() > 0) {
                    password.append(letters.charAt(0))
                            .append(letters.substring(1).toLowerCase());
                }

                if (numbers.length() > 0) {
                    password.append(numbers);
                }
            }
        }

        password.append("!");
        return password.toString();
    }
}