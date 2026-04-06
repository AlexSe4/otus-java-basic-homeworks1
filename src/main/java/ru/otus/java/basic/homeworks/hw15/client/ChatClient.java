package ru.otus.java.basic.homeworks.hw15.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public ChatClient() {
        scanner = new Scanner(System.in);
        try {
            socket = new Socket("localhost", 8189);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (ConnectException e) {
            System.out.println("Сервер не запущен");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.equals("/exitok")) {
                        System.out.println("Отключение от сервера...");
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Соединение с сервером потеряно.");
            } finally {
                disconnect();
                System.exit(0);
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();


        try {
            while (true) {
                String message = scanner.nextLine();
                out.writeUTF(message);
                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
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
}