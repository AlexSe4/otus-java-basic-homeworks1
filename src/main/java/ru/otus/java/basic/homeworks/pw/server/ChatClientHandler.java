package ru.otus.java.basic.homeworks.pw.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private final AuthenticatedProvider auth;
    private final RoomManager roomManager;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private int userId;
    private String currentRoom;
    private String role;
    private long lastActivityTime;
    private final SpamProtection spamProtection = new SpamProtection(5, 10);
    private volatile boolean isClosed = false;
    private ScheduledExecutorService inactivityScheduler;

    public ChatClientHandler(Socket socket, ChatServer server, AuthenticatedProvider auth, RoomManager roomManager) throws IOException {
        this.socket = socket;
        this.server = server;
        this.auth = auth;
        this.roomManager = roomManager;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.lastActivityTime = System.currentTimeMillis();

        new Thread(() -> {
            try {
                System.out.println("Подключение: " + socket.getPort());
                authenticateOrRegister();
                if (userId != 0) {
                    startInactivityMonitoring();
                    handleMessages();
                }
            } catch (IOException e) {
                System.out.println("Клиент отключился: " + username);
            } finally {
                disconnect();
            }
        }).start();
    }

    private void authenticateOrRegister() throws IOException {
        while (!isClosed) {
            sendMsg("Используйте /auth login password или /reg login password username");
            String message = in.readUTF();
            updateActivity();

            if (message.startsWith("/reg ")) {
                String[] parts = message.split("\\s+", 4);
                if (parts.length < 4) {
                    sendMsg("Неверный формат. Используйте: /reg <логин> <пароль> <имя>");
                } else if (auth.registration(this, parts[1], parts[2], parts[3])) {
                    sendMsg("/regok " + username);
                    enterDefaultRoom();
                    server.subscribe(this);
                    auth.loadChatHistory(this, currentRoom);
                    return;
                } else {
                    sendMsg("Ошибка регистрации. Логин или имя заняты.");
                }
            } else if (message.startsWith("/auth ")) {
                String[] parts = message.split("\\s+", 3);
                if (parts.length < 3) {
                    sendMsg("Неверный формат. Используйте: /auth <логин> <пароль>");
                } else {
                    String login = parts[1];
                    String password = parts[2];
                    String targetUsername = auth.getUsernameByLogin(login);
                    if (targetUsername != null && server.isUserActive(targetUsername)) {
                        sendMsg("Пользователь уже в чате с другого устройства.");
                    } else if (auth.authenticate(this, login, password)) {
                        sendMsg("/authok " + username);
                        enterDefaultRoom();
                        server.subscribe(this);
                        auth.loadChatHistory(this, currentRoom);
                        return;
                    } else {
                        sendMsg("Неверный логин/пароль или бан.");
                    }
                }
            } else if (message.equals("/exit")) {
                sendMsg("/exitok");
                return;
            } else {
                sendMsg("Неверная команда.");
            }
        }
    }

    private void enterDefaultRoom() {
        String lastRoom = auth.getLastRoomName(userId);
        if (lastRoom == null || auth.getRoomIdByName(lastRoom) == -1) lastRoom = "Общая комната";
        currentRoom = lastRoom;
        auth.enterRoom(this, currentRoom, null);
        auth.updateLastRoomName(userId, currentRoom);
    }
    //10	Отключение по неактивности (20 минут)
    private void startInactivityMonitoring() {
        inactivityScheduler = Executors.newSingleThreadScheduledExecutor();
        inactivityScheduler.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - lastActivityTime > 20 * 60 * 1000L) {
                sendMsg("Вы отключены за неактивность (20 мин).");
                disconnect();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
    //3 Получение от клиентов сообщений: служебных команд и сообщений чата
//6	Сервер добавляет время рассылки к сообщениям
    private void handleMessages() throws IOException {
        while (!isClosed) {
            String message = in.readUTF();
            updateActivity();

            if (message.startsWith("/")) {
                handleCommand(message);
            } else {
                if (spamProtection.checkAndRecord()) {
                    String filtered = BadWordsFilter.filter(message);
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String formatted = String.format("[%s] %s: %s", time, username, filtered);
                    auth.handleChatMessage(this, filtered);
                    server.broadcastToRoom(currentRoom, formatted, this);
                } else {
                    sendMsg("Слишком много сообщений. Подождите.");
                }
            }
        }
    }

    private void handleCommand(String cmd) {
        String[] parts = cmd.split("\\s+");
        switch (parts[0]) {
            case "/exit":
                sendMsg("/exitok");
                disconnect();
                break;
            case "/changenick":
                if (parts.length == 2 && auth.changeNickname(this, parts[1])) {
                    String old = username;
                    username = parts[1];
                    server.broadcastToRoom(currentRoom, "Пользователь " + old + " сменил ник на " + username, this);
                    sendMsg("Ник изменён.");
                } else sendMsg("Ник не может быть изменён (занят).");
                break;
            case "/w":   //Личные сообщения
                if (parts.length >= 3) {
                    String target = parts[1];
                    String msg = String.join(" ", List.of(parts).subList(2, parts.length));
                    ChatClientHandler targetClient = server.findClientByUsername(target);
                    if (targetClient != null) {
                        targetClient.sendMsg("[ЛС от " + username + "]: " + msg);
                        sendMsg("[ЛС для " + target + "]: " + msg);
                    } else sendMsg("Пользователь не в сети.");
                } else sendMsg("/w ник сообщение");
                break;
            case "/users":
                sendMsg("/activelist " + String.join(",", server.getActiveUsernames()));
                break;
            case "/help":
                sendHelp();
                break;
            case "/logout":
                logout();
                break;
            case "/whoami":
                sendMsg("Вы вошли как: " + username + ", роль: " + role);
                break;
            case "/lastactivity":
                if (parts.length == 2) {
                    String target = parts[1];
                    String activity = auth.getLastActivity(target);
                    sendMsg("Последняя активность " + target + ": " + activity);
                } else {
                    sendMsg("Использование: /lastactivity ник");
                }
                break;
            case "/kick":
                handleKick(parts);
                break;
            case "/ban":
                handleBan(parts);
                break;
            case "/unban":
                if (parts.length == 2 && Roles.isAdmin(role)) {
                    if (auth.removeBan(parts[1])) sendMsg("Бан снят.");
                    else sendMsg("Пользователь не найден.");
                } else sendMsg("Недостаточно прав.");
                break;
            case "/add_admin":
                if (parts.length == 2 && Roles.isAdmin(role)) {
                    auth.setAdmin(parts[1]);
                    ChatClientHandler target = server.findClientByUsername(parts[1]);
                    if (target != null) target.setRole(Roles.ROLE_ADMIN);
                    sendMsg("Пользователь " + parts[1] + " теперь администратор.");
                } else sendMsg("Недостаточно прав.");
                break;
            case "/remove_admin":
                if (parts.length == 2 && Roles.isAdmin(role)) {
                    auth.removeAdmin(parts[1]);
                    ChatClientHandler target = server.findClientByUsername(parts[1]);
                    if (target != null) target.setRole(Roles.ROLE_USER);
                    sendMsg("Права администратора сняты с пользователя " + parts[1] + ".");
                } else sendMsg("Недостаточно прав.");
                break;
            case "/shutdown":
                if (Roles.isAdmin(role)) server.shutdown();
                else sendMsg("Нет прав.");
                break;
            case "/createroom":
                if (parts.length >= 2) {
                    String name = parts[1];
                    String pass = parts.length >= 3 ? parts[2] : null;
                    if (auth.getUserRoomCount(userId) < 5) {
                        if (roomManager.createRoom(name, pass, userId)) sendMsg("Комната создана.");
                        else sendMsg("Комната уже существует.");
                    } else sendMsg("Достигнут лимит в 5 комнат.");
                } else sendMsg("/createroom имя [пароль]");
                break;
            case "/listrooms":
                List<String> pub = auth.getAllPublicRooms();
                List<String> created = auth.getUserCreatedRooms(userId);
                List<String> inv = auth.getUserInvitedRooms(userId);
                StringBuilder sb = new StringBuilder("Публичные: ").append(pub).append("\n");
                if (!created.isEmpty()) sb.append("Ваши: ").append(created).append("\n");
                if (!inv.isEmpty()) sb.append("Приглашены в: ").append(inv).append("\n");
                sendMsg(sb.toString());
                break;
            case "/enter":
                if (parts.length >= 2) {
                    String room = parts[1];
                    String pass = parts.length >= 3 ? parts[2] : null;
                    if (auth.enterRoom(this, room, pass)) {
                        String old = currentRoom;
                        currentRoom = room;
                        auth.updateLastRoomName(userId, room);
                        sendMsg("Вы вошли в комнату " + room);
                        auth.loadChatHistory(this, room);
                        server.broadcastToRoom(old, username + " покинул комнату", this);
                        server.broadcastToRoom(currentRoom, username + " вошёл в комнату", this);
                    } else sendMsg("Неверное имя или пароль.");
                } else sendMsg("/enter комната [пароль]");
                break;
            case "/leaveroom":
                if (!currentRoom.equals("Общая комната")) {
                    String old = currentRoom;
                    auth.leaveCurrentRoom(this);
                    auth.enterRoom(this, "Общая комната", null);
                    currentRoom = "Общая комната";
                    auth.updateLastRoomName(userId, currentRoom);
                    sendMsg("Вы вернулись в общую комнату.");
                    auth.loadChatHistory(this, currentRoom);
                    server.broadcastToRoom(old, username + " покинул комнату", this);
                } else sendMsg("Вы уже в общей комнате.");
                break;
            case "/deleteroom":
                if (parts.length == 2) {
                    String room = parts[1];
                    if (room.equals("Общая комната")) { sendMsg("Нельзя."); break; }
                    if (auth.deleteRoom(room, userId)) {
                        sendMsg("Комната удалена.");
                        server.broadcastToRoom(currentRoom, "Комната " + room + " удалена.", this);
                        if (currentRoom.equals(room)) {
                            currentRoom = "Общая комната";
                            auth.enterRoom(this, currentRoom, null);
                            auth.loadChatHistory(this, currentRoom);
                        }
                    } else sendMsg("Не удалось (не владелец или не существует).");
                }
                break;
            case "/invite":
                if (parts.length == 3) {
                    if (auth.inviteUserToRoom(parts[1], parts[2], userId)) {
                        ChatClientHandler target = server.findClientByUsername(parts[2]);
                        if (target != null) target.sendMsg("Приглашение в комнату " + parts[1]);
                        sendMsg("Приглашение отправлено.");
                    } else sendMsg("Ошибка.");
                }
                break;
            case "/myinvites":
                List<String> invites = auth.getInvitesForUser(userId);
                sendMsg(invites.isEmpty() ? "Нет приглашений." : "Приглашения: " + String.join(", ", invites));
                break;
            case "/decline":
                if (parts.length == 2) {
                    String roomName = parts[1];
                    Integer inviterId = auth.getInviterId(roomName, userId);
                    auth.removeInvite(roomName, userId);
                    sendMsg("Приглашение отклонено.");

                    if (inviterId != null) {
                        String inviterUsername = auth.getUsernameById(inviterId);
                        if (inviterUsername != null) {
                            ChatClientHandler inviter = server.findClientByUsername(inviterUsername);
                            if (inviter != null) {
                                inviter.sendMsg("Пользователь " + username + " отклонил приглашение в комнату " + roomName + ".");
                            }
                        }
                    }
                }
                break;
            case "/roominfo":
                String roomName = parts.length == 2 ? parts[1] : currentRoom;
                sendMsg(auth.getRoomInfo(roomName));
                break;
            default:
                sendMsg("Неизвестная команда. /help");
        }
    }

    private void handleKick(String[] parts) {
        if (parts.length < 2) { sendMsg("/kick ник"); return; }
        String targetName = parts[1];
        ChatClientHandler target = server.findClientByUsername(targetName);
        if (target == null) { sendMsg("Пользователь не в сети."); return; }

        if (currentRoom.equals("Общая комната") && Roles.isAdmin(role)) {
            server.kickUser(targetName, "Вас отключил администратор.");
        } else if (!currentRoom.equals("Общая комната") && auth.isRoomOwner(currentRoom, userId)) {
            if (target.getCurrentRoom().equals(currentRoom)) {
                auth.leaveCurrentRoom(target);
                auth.enterRoom(target, "Общая комната", null);
                target.setCurrentRoom("Общая комната");
                target.sendMsg("Вас исключили из комнаты.");
                sendMsg("Пользователь " + targetName + " исключён из комнаты.");
            } else sendMsg("Он не в этой комнате.");
        } else {
            sendMsg("Недостаточно прав.");
        }
    }

    private void handleBan(String[] parts) {
        if (!Roles.isAdmin(role)) { sendMsg("Нет прав."); return; }
        if (parts.length < 2) { sendMsg("/ban ник [минуты] [причина]"); return; }
        String target = parts[1];
        int minutes = 0;
        String reason = "";
        if (parts.length >= 3) {
            try { minutes = Integer.parseInt(parts[2]); } catch (NumberFormatException e) { reason = parts[2]; }
        }
        if (parts.length >= 4) reason = parts[3];
        LocalDateTime banEnd = minutes > 0 ? LocalDateTime.now().plusMinutes(minutes) : null;
        if (auth.addBan(target, banEnd, reason)) {
            ChatClientHandler targetClient = server.findClientByUsername(target);
            if (targetClient != null) {
                BanInfo banInfo = auth.getBanInfo(target);
                if (banInfo != null) {
                    targetClient.sendMsg(banInfo.getBanMessage());
                } else {
                    // запасной вариант, если по какой-то причине бан не прочитался
                    targetClient.sendMsg("Вы забанены: " + (banEnd != null ? "до " + banEnd : "бессрочно"));
                }
                targetClient.sendMsg("/banned");
            }
            sendMsg("Пользователь " + target + " забанен.");
        } else sendMsg("Ошибка.");
    }

    private void logout() {
        sendMsg("/logoutok");
        server.unsubscribe(this);
        username = null; userId = 0; currentRoom = null;
    }

    public void sendMsg(String msg) {
        try { out.writeUTF(msg); } catch (IOException e) { isClosed = true; }
    }

    private void sendHelp() {
        String helpHeader = "Ваша роль: " + role + "\n" +
                "Доступные команды:\n";
        sendMsg(helpHeader +
                "/auth login pass - вход\n" +
                "/reg login pass name - регистрация\n" +
                "/changenick newName - сменить ник\n" +
                "/w ник сообщение - личное сообщение\n" +
                "/users - список пользователей\n" +
                "/exit - выход\n" +
                "/kick ник - исключить из комнаты (админ/владелец)\n" +
                "/ban ник [минуты] [причина] - забанить (админ)\n" +
                "/unban ник - разбанить (админ)\n" +
                "/add_admin ник /remove_admin ник (админ)\n" +
                "/shutdown - выключить сервер (админ)\n" +
                "/createroom имя [пароль] - создать комнату\n" +
                "/enter имя [пароль] - войти\n" +
                "/leaveroom - покинуть\n" +
                "/deleteroom имя - удалить свою комнату\n" +
                "/invite комната пользователь\n" +
                "/myinvites - мои приглашения\n" +
                "/decline комната - отклонить\n" +
                "/roominfo [комната] - инфо о комнате");
    }

    private void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
        if (userId != 0) auth.updateUserActivity(userId);
    }

    public void disconnect() {
        isClosed = true;
        if (inactivityScheduler != null) inactivityScheduler.shutdownNow();
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
        server.unsubscribe(this);
    }

    public String getUsername() { return username; }
    public int getUserId() { return userId; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String room) { this.currentRoom = room; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}
