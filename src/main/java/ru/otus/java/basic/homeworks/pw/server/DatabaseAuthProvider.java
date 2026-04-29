package ru.otus.java.basic.homeworks.pw.server;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAuthProvider implements AuthenticatedProvider {
    private Connection connection;

    public DatabaseAuthProvider() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:./chatdb;AUTO_RECONNECT=TRUE", "sa", "");
            initTables();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось подключиться к H2", e);
        }
    }

    private void initTables() {
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, username VARCHAR(255) UNIQUE NOT NULL, " +
                    "last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            st.execute("CREATE TABLE IF NOT EXISTS roles (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) UNIQUE NOT NULL)");
            st.execute("MERGE INTO roles (id, name) KEY(id) VALUES (1, 'admin')");
            st.execute("MERGE INTO roles (id, name) KEY(id) VALUES (2, 'user')");
            st.execute("CREATE TABLE IF NOT EXISTS users_to_roles (" +
                    "user_id INT NOT NULL, role_id INT NOT NULL, PRIMARY KEY (user_id, role_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS bans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, " +
                    "ban_start TIMESTAMP NOT NULL, ban_end TIMESTAMP, reason VARCHAR(255), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255), owner_id INT NOT NULL, creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            st.execute("CREATE TABLE IF NOT EXISTS room_users (" +
                    "room_id INT NOT NULL, user_id INT NOT NULL, PRIMARY KEY (room_id, user_id), " +
                    "FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS room_messages (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, room_id INT NOT NULL, " +
                    "sender VARCHAR(255) NOT NULL, message TEXT NOT NULL, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS room_invites (" +
                    "room_id INT NOT NULL, user_id INT NOT NULL, invited_by INT NOT NULL, " +
                    "PRIMARY KEY (room_id, user_id), FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (invited_by) REFERENCES users(id))");
            st.execute("CREATE TABLE IF NOT EXISTS last_rooms (" +
                    "user_id INT PRIMARY KEY, room_name VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            st.execute("MERGE INTO rooms (name, owner_id, creation_date) KEY(name) VALUES ('Общая комната', 0, CURRENT_TIMESTAMP)");

            st.execute("DELETE FROM room_invites WHERE invited_by IN (SELECT id FROM users WHERE login = 'admin')");
            st.execute("DELETE FROM users_to_roles WHERE user_id IN (SELECT id FROM users WHERE login = 'admin')");
            st.execute("DELETE FROM users WHERE login = 'admin'");
            st.execute("INSERT INTO users (login, password, username) VALUES ('admin', 'admin', 'ADMIN')");
            st.execute("INSERT INTO users_to_roles (user_id, role_id) SELECT id, 1 FROM users WHERE login = 'admin'");

            System.out.println("База данных H2 инициализирована.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания таблиц", e);
        }
    }

    @Override
    public boolean authenticate(ChatClientHandler handler, String login, String password) {
        String sql = "SELECT id, username FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                if (isUserBanned(userId)) {
                    handler.sendMsg("Вы забанены.");
                    return false;
                }
                handler.setUserId(userId);
                handler.setUsername(rs.getString("username"));
                String role = getRoleForUser(userId);
                handler.setRole(role);
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public BanInfo getBanInfo(String username) {
        int userId = getUserIdByUsername(username);
        if (userId == -1) return null;
        String sql = "SELECT ban_end, reason FROM bans WHERE user_id = ? AND (ban_end IS NULL OR ban_end > CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp end = rs.getTimestamp("ban_end");
                LocalDateTime banEnd = end != null ? end.toLocalDateTime() : null;
                return new BanInfo(banEnd, rs.getString("reason"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    //4 Регистрация и авторизация с хранением в БД
    @Override
    public boolean registration(ChatClientHandler handler, String login, String password, String username) {
        String sql = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                assignRole(userId, Roles.ROLE_USER);
                handler.setUserId(userId);
                handler.setUsername(username);
                handler.setRole(Roles.ROLE_USER);
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    //8	Изменение ника
    @Override
    public boolean changeNickname(ChatClientHandler handler, String newNick) {
        int userId = handler.getUserId();
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE users SET username = ? WHERE id = ? AND NOT EXISTS (SELECT 1 FROM users WHERE username = ?)")) {
            ps.setString(1, newNick);
            ps.setInt(2, userId);
            ps.setString(3, newNick);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private String getRoleForUser(int userId) {
        String sql = "SELECT r.name FROM users_to_roles ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) { e.printStackTrace(); }
        return Roles.ROLE_USER;
    }

    @Override
    public void setAdmin(String username) {
        int userId = getUserIdByUsername(username);
        if (userId == -1) return;
        assignRole(userId, Roles.ROLE_ADMIN);
    }

    @Override
    public void removeAdmin(String username) {
        int userId = getUserIdByUsername(username);
        if (userId == -1) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM users_to_roles WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = 'admin')")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        assignRole(userId, Roles.ROLE_USER);
    }

    private void assignRole(int userId, String roleName) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users_to_roles (user_id, role_id) VALUES (?, (SELECT id FROM roles WHERE name = ?)) ON DUPLICATE KEY UPDATE role_id = VALUES(role_id)")) {
            ps.setInt(1, userId);
            ps.setString(2, roleName);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public boolean addBan(String usernameToBan, LocalDateTime banEnd, String reason) {
        int userId = getUserIdByUsername(usernameToBan);
        if (userId == -1) return false;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO bans (user_id, ban_start, ban_end, reason) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(3, banEnd != null ? Timestamp.valueOf(banEnd) : null);
            ps.setString(4, reason);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean removeBan(String usernameToUnban) {
        int userId = getUserIdByUsername(usernameToUnban);
        if (userId == -1) return false;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM bans WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private boolean isUserBanned(int userId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM bans WHERE user_id = ? AND (ban_end IS NULL OR ban_end > CURRENT_TIMESTAMP)")) {
            ps.setInt(1, userId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public int getUserIdByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    @Override
    public String getUsernameByLogin(String login) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE login = ?")) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public Integer getRoomIdByName(String name) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM rooms WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean createRoom(String name, String password, int ownerId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO rooms (name, password, owner_id) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, password);
            ps.setInt(3, ownerId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    //15.4 Вход в комнату по /enter [room] [pass]
    @Override
    public boolean enterRoom(ChatClientHandler client, String roomName, String password) {
        int userId = client.getUserId();
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) return false;
        try (PreparedStatement ps = connection.prepareStatement("SELECT password FROM rooms WHERE id = ?")) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedPass = rs.getString("password");
                if (storedPass != null && !storedPass.equals(password)) return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
        leaveCurrentRoom(client);
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO room_users (room_id, user_id) VALUES (?, ?)")) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return false; }
        return true;
    }
    //15.5	Один пользователь – одна комната
    @Override
    public void leaveCurrentRoom(ChatClientHandler client) {
        Integer roomId = getRoomIdByName(client.getCurrentRoom());
        if (roomId == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM room_users WHERE room_id = ? AND user_id = ?")) {
            ps.setInt(1, roomId);
            ps.setInt(2, client.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public boolean deleteRoom(String roomName, int userId) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null || !isRoomOwner(roomName, userId)) return false;
        try {
            connection.setAutoCommit(false);
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("DELETE FROM room_users WHERE room_id = " + roomId);
                st.executeUpdate("DELETE FROM room_messages WHERE room_id = " + roomId);
                st.executeUpdate("DELETE FROM room_invites WHERE room_id = " + roomId);
                st.executeUpdate("DELETE FROM rooms WHERE id = " + roomId);
            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRoomOwner(String roomName, int userId) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) return false;
        try (PreparedStatement ps = connection.prepareStatement("SELECT owner_id FROM rooms WHERE id = ?")) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("owner_id") == userId;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void handleChatMessage(ChatClientHandler sender, String message) {
        Integer roomId = getRoomIdByName(sender.getCurrentRoom());
        if (roomId == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO room_messages (room_id, sender, message) VALUES (?, ?, ?)")) {
            ps.setInt(1, roomId);
            ps.setString(2, sender.getUsername());
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE rooms SET last_activity = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    //15.6	История сообщений при подключении
    @Override
    public void loadChatHistory(ChatClientHandler client, String roomName) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT sender, message, timestamp FROM room_messages WHERE room_id = ? ORDER BY timestamp DESC LIMIT 10")) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            java.util.LinkedList<String> history = new java.util.LinkedList<>();
            while (rs.next()) {
                String t = rs.getTimestamp("timestamp").toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                history.addFirst(String.format("[%s] %s: %s", t, rs.getString("sender"), rs.getString("message")));
            }
            for (String msg : history) client.sendMsg(msg);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public boolean inviteUserToRoom(String roomName, String inviteeUsername, int inviterId) {
        int inviteeId = getUserIdByUsername(inviteeUsername);
        Integer roomId = getRoomIdByName(roomName);
        if (inviteeId == -1 || roomId == null) return false;
        try (PreparedStatement ps = connection.prepareStatement(
                "MERGE INTO room_invites (room_id, user_id, invited_by) KEY(room_id, user_id) VALUES (?, ?, ?)")) {
            ps.setInt(1, roomId);
            ps.setInt(2, inviteeId);
            ps.setInt(3, inviterId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<String> getInvitesForUser(int userId) {
        List<String> rooms = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT r.name FROM rooms r JOIN room_invites ri ON r.id = ri.room_id WHERE ri.user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(rs.getString("name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    @Override
    public void removeInvite(String roomName, int userId) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM room_invites WHERE room_id = ? AND user_id = ?")) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<String> getAllPublicRooms() {
        List<String> rooms = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM rooms")) {
            while (rs.next()) rooms.add(rs.getString("name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    @Override
    public List<String> getUserCreatedRooms(int userId) {
        List<String> rooms = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT name FROM rooms WHERE owner_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(rs.getString("name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    @Override
    public List<String> getUserInvitedRooms(int userId) {
        return getInvitesForUser(userId);
    }
    //15.8	Владелец комнаты, лимит 5 комнат
    @Override
    public int getUserRoomCount(int userId) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM rooms WHERE owner_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public void updateUserActivity(int userId) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE users SET last_activity = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    // 18 /lastactivity – последняя активность
    @Override
    public String getLastActivity(String username) {
        String sql = "SELECT last_activity FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("last_activity");
                if (ts == null) return "неизвестно";
                return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "пользователь не найден";
    }

    @Override
    public String getLastRoomName(int userId) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT room_name FROM last_rooms WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("room_name");
        } catch (SQLException e) { e.printStackTrace(); }
        return "Общая комната";
    }

    @Override
    public void updateLastRoomName(int userId, String roomName) {
        try (PreparedStatement ps = connection.prepareStatement(
                "MERGE INTO last_rooms (user_id, room_name) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setString(2, roomName);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteOldRooms() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM rooms WHERE last_activity < DATEADD('DAY', -7, CURRENT_TIMESTAMP) AND name != 'Общая комната'");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public String getRoomInfo(String roomName) {
        String sql = "SELECT r.name, u.username AS owner, r.password, r.creation_date FROM rooms r " +
                "LEFT JOIN users u ON r.owner_id = u.id WHERE r.name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roomName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String owner = rs.getString("owner");
                boolean hasPass = rs.getString("password") != null;
                Timestamp created = rs.getTimestamp("creation_date");
                return String.format("Комната: %s, Владелец: %s, Пароль: %s, Создана: %s",
                        roomName, owner != null ? owner : "система", hasPass ? "есть" : "нет", created);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Комната не найдена.";
    }

    // Новый метод: получение ID пригласившего
    @Override
    public Integer getInviterId(String roomName, int inviteeId) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) return null;
        String sql = "SELECT invited_by FROM room_invites WHERE room_id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, inviteeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("invited_by");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Новый метод: получение ника по ID
    @Override
    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void close() {
        try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
