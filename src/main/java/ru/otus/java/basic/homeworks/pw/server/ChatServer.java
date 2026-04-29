package ru.otus.java.basic.homeworks.pw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatServer {
    private final int port;
    private final CopyOnWriteArrayList<ChatClientHandler> clients = new CopyOnWriteArrayList<>();
    private final AuthenticatedProvider auth;
    private final RoomManager roomManager;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);  //10 потоков
    private volatile boolean running = true;

    // Множество активных имён пользователей для предотвращения повторного входа
    private final Set<String> activeUsernames = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
        this.auth = new DatabaseAuthProvider();
        this.roomManager = new RoomManager(auth);
        // п. 15.7 — удаление старых неактивных комнат
        new Thread(() -> {
            while (running) {
                try { Thread.sleep(24 * 60 * 60 * 1000L); } catch (InterruptedException e) { break; }
                roomManager.syncAfterDeletion(clients);
            }
        }).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (running) {
                Socket socket = serverSocket.accept();
                pool.execute(() -> {
                    try {
                        new ChatClientHandler(socket, this, auth, roomManager);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void subscribe(ChatClientHandler client) {
        clients.add(client);
        String room = client.getCurrentRoom();
        if (room != null) {
            broadcastToRoom(room, client.getUsername() + " вошёл в комнату", client);
        }
        addActiveUser(client.getUsername());
        broadcastActiveList();
    }

    public void unsubscribe(ChatClientHandler client) {
        clients.remove(client);
        String room = client.getCurrentRoom();
        if (room != null) {
            broadcastToRoom(room, client.getUsername() + " покинул комнату", client);
        }
        removeActiveUser(client.getUsername());
        broadcastActiveList();
    }
    //5	Рассылка сообщений всем авторизованным клиентам
    public void broadcastToRoom(String room, String msg, ChatClientHandler sender) {
        for (ChatClientHandler c : clients) {
            if (c.getCurrentRoom().equals(room) && c != sender) {
                c.sendMsg(msg);
            }
        }
    }

    public void kickUser(String username, String reason) {
        ChatClientHandler target = findClientByUsername(username);
        if (target != null) {
            target.sendMsg("/kickoff");
            target.sendMsg(reason);
            target.disconnect();
        }
    }

    public List<String> getActiveUsernames() {
        return clients.stream().map(ChatClientHandler::getUsername).collect(Collectors.toList());
    }

    public ChatClientHandler findClientByUsername(String username) {
        return clients.stream().filter(c -> c.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    private void broadcastActiveList() {
        String list = "/activelist " + String.join(",", getActiveUsernames());
        for (ChatClientHandler c : clients) c.sendMsg(list);
    }
    //13	Остановка сервера командой /shutdown
    public void shutdown() {
        running = false;
        for (ChatClientHandler c : clients) c.disconnect();
        pool.shutdown();
        try { if (!pool.awaitTermination(5, TimeUnit.SECONDS)) pool.shutdownNow(); } catch (InterruptedException e) { pool.shutdownNow(); }
        ((DatabaseAuthProvider) auth).close();
        System.out.println("Сервер остановлен.");
    }

    // Методы для контроля уникальности входа
    public synchronized boolean isUserActive(String username) {
        return activeUsernames.contains(username);
    }
    public synchronized void addActiveUser(String username) {
        activeUsernames.add(username);
    }
    public synchronized void removeActiveUser(String username) {
        activeUsernames.remove(username);
    }
}
