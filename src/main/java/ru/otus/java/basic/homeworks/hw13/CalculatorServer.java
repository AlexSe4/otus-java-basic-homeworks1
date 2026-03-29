package ru.otus.java.basic.homeworks.hw13;

import java.io.*;
import java.net.*;

public class CalculatorServer {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер запущен на порту " + SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private static final String OPERATIONS = "Доступные операции: +, -, *, /";

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                out.writeUTF(OPERATIONS);
                out.flush();

                while (true) {
                    String request = in.readUTF();
                    System.out.println("Запрос от клиента: " + request);
                    String result = processRequest(request);
                    out.writeUTF(result);
                    out.flush();
                }
            } catch (EOFException e) {
                System.out.println("Клиент отключился: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Ошибка обмена с клиентом: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Ошибка закрытия сокета: " + e.getMessage());
                }
            }
        }

        private String processRequest(String request) {
            request = request.trim();
            String[] parts = request.split("\\s+");

            if (parts.length != 3) {
                return "Ошибка: неверный формат. Используйте: число1 оператор число2";
            }

            double a, b;
            try {
                a = Double.parseDouble(parts[0]);
                b = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                return "Ошибка: неверный формат числа";
            }

            String operator = parts[1];
            double result;

            switch (operator) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b == 0) return "Ошибка: деление на ноль";
                    result = a / b;
                    break;
                default:
                    return "Ошибка: неизвестный оператор. Доступны: +, -, *, /";
            }
            return String.valueOf(result);
        }
    }
}
