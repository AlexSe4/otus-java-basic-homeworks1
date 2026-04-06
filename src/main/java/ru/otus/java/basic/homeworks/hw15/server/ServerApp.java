package ru.otus.java.basic.homeworks.hw15.server;

public class ServerApp {
    public static void main(String[] args) {
        new ChatServer(8189).start();
    }
}
