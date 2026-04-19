package ru.otus.java.basic.homeworks.hw16.server;

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

    public void start() {
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

    public synchronized void subscribe(ChatClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastMessage("В чат вошел: " + clientHandler.getUsername() +
                " (роль: " + clientHandler.getRole() + ")", clientHandler);
    }

    public synchronized void unsubscribe(ChatClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUsername(), clientHandler);
    }

    public void broadcastMessage(String message, ChatClientHandler sender) {
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
        for (ChatClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equalsIgnoreCase(username)) {
                return clientHandler;
            }
        }
        return null;
    }

    public boolean isUsernameUnique(String username) {
        for (ChatClientHandler clientHandler : clients) {
            if (clientHandler.getUsername() != null && clientHandler.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyClientConnected() {
        return !clients.isEmpty();
    }

    public boolean kickUser(String username, String adminName) {
        ChatClientHandler target = findClientByUsername(username);
        if (target == null) {
            return false;
        }
        target.sendMsg("/kicked");
        target.sendMsg("Вас отключил администратор " + adminName);
        unsubscribe(target);
        target.disconnect();
        broadcastMessage("Пользователь " + username + " был отключен администратором " + adminName, null);
        return true;
    }
}
