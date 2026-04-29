package ru.otus.java.basic.homeworks.pw.server;

public class ServerApp {
    public static void main(String[] args) {
        new ChatServer(9090).start();
    }
}
