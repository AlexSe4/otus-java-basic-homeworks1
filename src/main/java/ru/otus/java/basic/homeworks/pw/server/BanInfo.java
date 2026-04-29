package ru.otus.java.basic.homeworks.pw.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanInfo {
    private final LocalDateTime banEnd;
    private final String reason;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public BanInfo(LocalDateTime banEnd, String reason) {
        this.banEnd = banEnd;
        this.reason = reason;
    }

    public boolean isPermanent() {
        return banEnd == null;
    }

    //перманентный бан и временный бан   /ban
    public String getBanMessage() {
        if (isPermanent()) {
            return "Вы забанены навсегда. Причина: " + reason;
        }
        return "Вы забанены до " + banEnd.format(fmt) + ". Причина: " + reason;
    }
}
