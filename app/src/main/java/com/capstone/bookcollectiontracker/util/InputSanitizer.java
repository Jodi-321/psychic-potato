package com.capstone.bookcollectiontracker.util;

import java.util.regex.Pattern;

public class InputSanitizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{10}|\\d{13})$");
    private static final String[] SQL_KEYWORDS = {
            "select", "insert", "update", "delete", "drop", "alter", "--", ";",
            "union", "exec", "execute", "sp_", "xp_", "syscolumns", "sysobjects",
            "waitfor", "delay", "benchmark"
    };

    private static final String[] SQL_INJECTION_PATTERNS = {
            "1=1", "1 = 1",
            "OR 1=1", "OR 1 = 1",
            "UNION SELECT",
            "WAITFOR DELAY",
            "'; EXEC", "'; EXECUTE",
            "/**/", "/*!",
            "AS SYSTEM_USER", "AS SYSADMIN"
    };

    public static boolean isValidIsbn(String isbn) {
        return ISBN_PATTERN.matcher(isbn).matches();
    }
    public static boolean containsSqlKeywords(String input) {
        if (input == null) return false;
        String lowerCaseInput = input.toLowerCase();
        for (String keyword : SQL_KEYWORDS) {
            if (lowerCaseInput.contains(keyword)) {
                return true;
            }
        }

        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (lowerCaseInput.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }
}
