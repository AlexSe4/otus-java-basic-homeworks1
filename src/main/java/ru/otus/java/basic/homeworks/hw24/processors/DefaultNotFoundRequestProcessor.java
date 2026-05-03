package ru.otus.java.basic.homeworks.hw24.processors;

import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;


public class DefaultNotFoundRequestProcessor implements RequestProcessor {
    @Override
    public HttpResponse execute(HttpRequest request) {
        HttpResponse response = new HttpResponse(404, "Not Found");
        response.setHeader("Content-Type", "text/html");
        response.setBody("<html><body><h1>404.. Page Not Found</h1></body></html>");
        return response;
    }
}
