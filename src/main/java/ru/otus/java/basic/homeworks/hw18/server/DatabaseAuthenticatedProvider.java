package ru.otus.java.basic.homeworks.hw18.server;

import java.sql.*;
import java.util.*;

public class DatabaseAuthenticatedProvider implements AuthenticatedProvider {
    private static final String DB_URL = "jdbc:sqlite:chat.db";
    private static final String REGISTRATION_QUERY = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
    private static final String AUTHENTICATION_QUERY = "SELECT password, id, username FROM users WHERE login = ?";
    private Server server;
    private Connection connection;

    public DatabaseAuthenticatedProvider(Server server) throws SQLException {
        this.server = server;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        this.connection = DriverManager.getConnection(DB_URL);
        enableForeignKeys();
    }

    private void enableForeignKeys() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
    }

    @Override
    public void initialize() {
        System.out.println("Инициализация DatabaseAuthenticatedProvider (SQLite)");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS roles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "password TEXT NOT NULL, " +
                    "login TEXT NOT NULL UNIQUE, " +
                    "username TEXT NOT NULL UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS users_to_roles (" +
                    "user_id INTEGER NOT NULL, " +
                    "role_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (user_id, role_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE)");


            stmt.execute("INSERT OR IGNORE INTO roles (name) VALUES ('admin')");
            stmt.execute("INSERT OR IGNORE INTO roles (name) VALUES ('user')");

            System.out.println("Таблицы и роли успешно инициализированы.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка инициализации базы данных");
        }
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String dbPassword = null;
        int userId = -1;
        String username = null;

        try (PreparedStatement ps = connection.prepareStatement(AUTHENTICATION_QUERY)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dbPassword = rs.getString("password");
                    userId = rs.getInt("id");
                    username = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientHandler.sendMsg("Ошибка базы данных при аутентификации.");
            return false;
        }

        if (server.isUserActive(username)) {
            clientHandler.sendMsg("Пользователь уже аутентифицирован на другом устройстве.");
            return false;
        }

        if (dbPassword == null || !dbPassword.equals(password)) {
            clientHandler.sendMsg("Неверный логин/пароль");
            return false;
        }

        List<Roles> userRoles = getRoleForUser(userId);

        if (!userRoles.contains(Roles.ADMIN) && !userRoles.contains(Roles.USER)) {
            clientHandler.sendMsg("У вас нет прав для входа.");
            return false;
        }

        clientHandler.setUsername(username);
        server.addActiveUser(username);

        if (userRoles.contains(Roles.ADMIN)) {
            clientHandler.setRole(Roles.ADMIN);
        } else {
            clientHandler.setRole(Roles.USER);
        }
        clientHandler.sendMsg("/authok " + username);
        return true;
    }

    private List<Roles> getRoleForUser(int userId) {
        List<Roles> roles = new ArrayList<>();
        String query = "SELECT r.name FROM roles r JOIN users_to_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String roleName = rs.getString("name");
                    roles.add(Roles.valueOf(roleName.toUpperCase()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        Roles role = Roles.USER;

        if (login.length() < 3 || password.length() < 3 || username.length() < 3) {
            clientHandler.sendMsg("Логин 3+ символа, пароль 3+ символа, имя пользователя 3+ символа");
            return false;
        }

        String availabilityMessage = checkUsernameAvailability(login, username);
        if (availabilityMessage != null) {
            clientHandler.sendMsg(availabilityMessage);
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(REGISTRATION_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                setRoleForUser(userId, role);
                clientHandler.setUsername(username);
                clientHandler.setRole(role);
                clientHandler.sendMsg("/regok " + username);
                return true;
            }
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка регистрации: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean addAdmin(ClientHandler clientHandler, String username, ClientHandler newAdminHandler) {
        Roles roles = clientHandler.getRole();
        if (roles != Roles.ADMIN) {
            clientHandler.sendMsg("У вас нет прав для добавления администратора.");
            return false;
        }

        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            clientHandler.sendMsg("Пользователь с таким именем не найден.");
            return false;
        }
        if (roleExists(userId, Roles.ADMIN)) {
            clientHandler.sendMsg("Пользователь " + username + " уже имеет роль администратора.");
            return false;
        }

        try {
            setRoleForUser(userId, Roles.ADMIN);
            clientHandler.sendMsg("Пользователь " + username + " успешно добавлен как администратор.");
            if (newAdminHandler != null) {
                newAdminHandler.sendMsg("\nПоздравляем! Вы были назначены администратором!\n" +
                        "Ваши права уже вступили в силу.\n");
            }
            return true;
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при добавлении администратора: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeAdminRole(ClientHandler clientHandler, String username, ClientHandler clientToRemove) {
        Roles roles = clientHandler.getRole();
        if (roles != Roles.ADMIN) {
            clientHandler.sendMsg("У вас нет прав для удаления администратора.");
            return false;
        }

        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            clientHandler.sendMsg("Пользователь с таким именем не найден.");
            return false;
        }
        if (!roleExists(userId, Roles.ADMIN)) {
            clientHandler.sendMsg("У пользователя " + username + " нет роли администратора.");
            return false;
        }

        String query = "DELETE FROM users_to_roles " +
                "WHERE user_id = ? " +
                "AND role_id = (SELECT id FROM roles WHERE name = 'admin')";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                clientHandler.sendMsg("Роль администратора у пользователя " + username + " успешно удалена.");
                if (clientToRemove != null) {
                    clientToRemove.sendMsg("\nУведомляем вас, что ваша роль администратора была удалена!\n" +
                            "Теперь вы обладаете правами обычного пользователя и не сможете выполнять административные действия.\n");
                }
                return true;
            }
        } catch (SQLException e) {
            clientHandler.sendMsg("Произошла ошибка при удалении роли администратора. Пожалуйста, попробуйте позже.");
            return false;
        }
        return false;
    }

    private int getUserIdByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    void setRoleForUser(int userId, Roles role) throws SQLException {
        String sql = "INSERT INTO users_to_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, getRoleId(role));
            ps.executeUpdate();
        }
    }

    private int getRoleId(Roles role) throws SQLException {
        String sql = "SELECT id FROM roles WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.name().toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Role not found: " + role.name());
    }

    private String checkUsernameAvailability(String login, String username) {
        boolean isLoginTaken = isFieldTaken("login", login);
        boolean isUsernameTaken = isFieldTaken("username", username);

        if (isLoginTaken && isUsernameTaken) {
            return "Логин и имя пользователя уже заняты!";
        } else if (isLoginTaken) {
            return "Логин уже занят!";
        } else if (isUsernameTaken) {
            return "Имя пользователя уже занято!";
        }
        return null;
    }

    private boolean isFieldTaken(String fieldName, String value) {
        String query = "SELECT COUNT(*) FROM users WHERE " + fieldName + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean roleExists(int userId, Roles role) {
        String query = "SELECT COUNT(*) FROM users_to_roles WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, role.name().toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
