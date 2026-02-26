package com.ravenbill.vanilla;

public final class Config {

    public static final String API_URL = env("VANILLA_API_URL", "http://localhost:4000");
    public static final String EMAIL = requireEnv("VANILLA_EMAIL");
    public static final String PASSWORD = requireEnv("VANILLA_PASSWORD");
    public static final String ACCOUNT_ID = requireEnv("VANILLA_ACCOUNT_ID");

    private Config() {}

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }
}
