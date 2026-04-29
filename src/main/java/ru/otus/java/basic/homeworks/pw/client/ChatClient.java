package ru.otus.java.basic.homeworks.pw.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final String host = "localhost";
    private final int port = 9090;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public ChatClient() {
        scanner = new Scanner(System.in);
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (ConnectException e) {
            System.out.println("Сервер не запущен. Пожалуйста, запустите сервер и попробуйте снова.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Приветственное сообщение
        System.out.println("     ДОБРО ПОЖАЛОВАТЬ В СЕТЕВОЙ ЧАТ!");
        System.out.println("Этот чат поддерживает:");
        System.out.println("Общение в реальном времени в общей комнате");
        System.out.println("Приватные комнаты с паролем и без");
        System.out.println("Личные сообщения, смену ника, систему ролей");
        System.out.println("Баны, приглашения, историю сообщений");
        System.out.println();
        System.out.println("Для начала работы необходимо пройти регистрацию или авторизацию:");
        System.out.println("  /reg логин пароль имя   — создание нового аккаунта");
        System.out.println("  /auth логин пароль       — вход в существующий аккаунт");
        System.out.println();
        System.out.println("Полный список команд доступен после входа по команде /help");
        System.out.println();

        // Сообщения от сервера
        new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    String message = in.readUTF();
                    processServerMessage(message);
                }
            } catch (IOException e) {
                System.out.printf("Не удалось установить соединение с сервером.\n" +
                        "Пожалуйста, проверьте, запущен ли сервер и доступен ли он по адресу %s:%s.\n", host, port);
            } finally {
                disconnect();
                System.exit(0);
            }
        }).start();

        try {
            while (true) {
                if (socket.isClosed()) {
                    System.out.println("Вы были отключены от чата.");
                    break;
                }
                String message = scanner.nextLine();
                if (message.trim().isEmpty()) {
                    System.out.println("Сообщение не может быть пустым.");
                    continue;
                }
                if (socket.isClosed()) {
                    System.out.println("Вы были отключены от чата.");
                    break;
                }
                try {
                    out.writeUTF(message);
                } catch (IOException e) {
                    System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private void processServerMessage(String message) {
        if (message.startsWith("/authok ")) {
            System.out.println("Добро пожаловать в чат, " + message.split(" ")[1] + "!");
        } else if (message.startsWith("/regok ")) {
            System.out.println("Регистрация успешна, добро пожаловать, " + message.split(" ")[1] + "!");
        } else if (message.equals("/exitok")) {
            System.out.println("Вы вышли из чата.");
            disconnect();
            System.exit(0);
        } else if (message.equals("/logoutok")) {
            System.out.println("Вы вышли из системы. Пожалуйста, введите данные для повторной аутентификации.");
        } else if (message.startsWith("/kickoff")) {
            System.out.println("Вас отключил администратор.");
            try {
                out.writeUTF("/exit");
            } catch (IOException ignored) {}
            disconnect();
            System.exit(0);
        } else if (message.startsWith("/banned")) {
            System.out.println("Вы забанены и отключены от сервера.");
            disconnect();
            System.exit(0);
        } else if (message.startsWith("/activelist ")) {
            String[] users = message.substring(12).split(",");
            System.out.println("Активные пользователи: " + String.join(", ", users));
        } else {
            System.out.println(message);
        }
    }

    private void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении соединения: " + e.getMessage());
        }
    }
}
