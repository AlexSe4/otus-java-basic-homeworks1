package ru.otus.java.basic.homeworks.hw24.processors;

import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;


public class HelloRequestProcessor implements RequestProcessor {
    @Override
    public HttpResponse execute(HttpRequest request) {
        HttpResponse response = new HttpResponse(200, "OK");
        response.setHeader("Content-Type", "text/html");
        response.setBody("<html><body><h1>Hello World!!!</h1></body></html>");
        return response;
    }
}