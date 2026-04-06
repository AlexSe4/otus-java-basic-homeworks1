package ru.otus.java.basic.homeworks.hw15.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private int port;
    private CopyOnWriteArrayList<ChatClientHandler> clients;

    public ChatServer(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Чат-сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ChatClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ChatClientHandler chatClientHandler){
        clients.add(chatClientHandler);
        broadcastMessage("В чат вошел: " + chatClientHandler.getUsername(), chatClientHandler);
    }

    public synchronized void unsubscribe(ChatClientHandler chatClientHandler){
        clients.remove(chatClientHandler);
        broadcastMessage("Из чата вышел: "+ chatClientHandler.getUsername(), chatClientHandler);
    }

    public void broadcastMessage(String message, ChatClientHandler sender){
        for (ChatClientHandler client : clients) {
            if (client != sender) {
                client.sendMsg(message);
            }
        }
    }

    public List<String> getActiveUsernames() {
        List<String> activeUsernames = new ArrayList<>();
        for (ChatClientHandler client : clients) {
            activeUsernames.add(client.getUsername());
        }
        return activeUsernames;
    }

    public ChatClientHandler findClientByUsername(String username) {
        for (ChatClientHandler chatClientHandler : clients) {
            if (chatClientHandler.getUsername().equalsIgnoreCase(username)) {
                return chatClientHandler;
            }
        }
        return null;
    }

    public boolean isUsernameUnique(String username) {
        for (ChatClientHandler chatClientHandler : clients) {
            if (chatClientHandler.getUsername() != null && chatClientHandler.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }
}
