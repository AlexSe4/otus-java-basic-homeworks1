package ru.otus.java.basic.homeworks.hw16.server;

public class ChatServerApp {
    public static void main(String[] args) {
        new ChatServer(8189).start();
    }
}
