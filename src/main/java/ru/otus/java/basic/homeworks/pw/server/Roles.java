package ru.otus.java.basic.homeworks.pw.server;

//11	Роли admin и user
public class Roles {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    public static boolean isAdmin(String role) {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }
}
