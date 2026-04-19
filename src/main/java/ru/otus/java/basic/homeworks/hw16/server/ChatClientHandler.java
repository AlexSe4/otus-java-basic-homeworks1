package ru.otus.java.basic.homeworks.hw16.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ChatClientHandler {
    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private String role;

    public ChatClientHandler(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                System.out.println("Клиент подключился " + socket.getPort());
                requestUsername();
                handleMessages();
            } catch (IOException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } finally {
                disconnect();
                server.unsubscribe(this);
            }
        }).start();
    }

    private void requestUsername() throws IOException {
        while (true) {
            sendMsg("Введите имя пользователя:");
            String requestedUsername = in.readUTF();
            if (server.isUsernameUnique(requestedUsername)) {
                username = requestedUsername;
                boolean isFirst = !server.isAnyClientConnected();
                role = Roles.getDefaultRole(isFirst);

                String welcomeMsg = "Добро пожаловать, " + username + "! Ваша роль: " + role;
                if (Roles.isAdmin(role)) {
                    welcomeMsg += "\n/help - список команд (доступен /kick)";
                } else {
                    welcomeMsg += "\n/help - список команд";
                }
                sendMsg(welcomeMsg);
                server.subscribe(this);
                sendActiveUsers();
                break;
            } else {
                sendMsg("Имя пользователя уже занято. Выберите другое.");
            }
        }
    }

    private void handleMessages() {
        try {
            while (true) {
                String message = in.readUTF();

                if (message.startsWith("/w ")) {
                    sendPrivateMessage(message);
                } else if (message.equalsIgnoreCase("/users")) {
                    sendActiveUsers();
                } else if (message.equalsIgnoreCase("/help")) {
                    sendHelpMessage();
                } else if (message.startsWith("/kick ")) {
                    handleKickCommand(message);
                } else if (message.startsWith("/")) {
                    if (message.equalsIgnoreCase("/exit")) {
                        sendMsg("/exitok");
                        System.out.println("Клиент отключился: " + username);
                        break;
                    } else {
                        sendMsg("Неизвестная команда. Введите /help.");
                    }
                } else {
                    sendMsg("Вы: " + message);
                    server.broadcastMessage(username + ": " + message, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Клиент отключился: " + username);
        } finally {
            disconnect();
        }
    }

    private void handleKickCommand(String message) {
        if (!Roles.canKick(role)) {
            sendMsg("У вас недостаточно прав для использования команды /kick.");
            return;
        }

        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            sendMsg("Использование: /kick <никнейм>");
            return;
        }
        String targetNick = parts[1].trim();
        if (targetNick.equalsIgnoreCase(username)) {
            sendMsg("Нельзя отключить самого себя.");
            return;
        }

        ChatClientHandler target = server.findClientByUsername(targetNick);
        if (target == null) {
            sendMsg("Пользователь " + targetNick + " не найден.");
            return;
        }
        if (Roles.isAdmin(target.getRole())) {
            sendMsg("Нельзя отключить другого администратора.");
            return;
        }

        boolean kicked = server.kickUser(targetNick, username);
        if (kicked) {
            sendMsg("Пользователь " + targetNick + " был отключен.");
        } else {
            sendMsg("Не удалось отключить пользователя " + targetNick);
        }
    }

    private void sendPrivateMessage(String message) {
        String[] tokens = message.split(" ", 3);
        if (tokens.length < 3) {
            sendMsg("Использование: /w <имя> <сообщение>");
            return;
        }

        String targetUsername = tokens[1];
        String privateMessage = tokens[2];

        if (targetUsername.equalsIgnoreCase(username)) {
            sendMsg("Нельзя отправить сообщение самому себе.");
            return;
        }

        ChatClientHandler targetClient = server.findClientByUsername(targetUsername);
        if (targetClient != null) {
            targetClient.sendMsg("[Private от " + username + "]: " + privateMessage);
            sendMsg("[Вы -> " + targetUsername + "]: " + privateMessage);
        } else {
            sendMsg("Пользователь " + targetUsername + " не найден.");
        }
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendActiveUsers() {
        List<String> activeUsernames = server.getActiveUsernames();
        sendMsg("Текущие пользователи: " + String.join(", ", activeUsernames));
    }

    private void sendHelpMessage() {
        String help = "Доступные команды:\n" +
                "/users - список пользователей\n" +
                "/w <ник> <сообщение> - личное сообщение\n" +
                "/exit - выход из чата\n" +
                "/help - эта справка";
        if (Roles.canKick(role)) {
            help += "\n/kick <ник> - отключить пользователя (только для администратора)";
        }
        sendMsg(help);
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}