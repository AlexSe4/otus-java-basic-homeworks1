package ru.otus.java.basic.homeworks.hw24;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            try (Socket socket = serverSocket.accept()) {
                byte[] buffer = new byte[8192];
                int n = socket.getInputStream().read(buffer);
                if (n < 1) {
                    return;
                }
                String rawRequest = new String(buffer, 0, n);
                HttpRequest request = new HttpRequest(rawRequest);
                request.info(true);

                HttpResponse response = dispatcher.execute(request);
                socket.getOutputStream().write(response.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

