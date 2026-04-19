package ru.otus.java.basic.homeworks.hw16.server;

public class Roles {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    public static boolean canKick(String role) {
        return ROLE_ADMIN.equals(role);
    }

    public static String getDefaultRole(boolean isFirstClient) {
        return isFirstClient ? ROLE_ADMIN : ROLE_USER;
    }

    public static boolean isAdmin(String role) {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }
}
