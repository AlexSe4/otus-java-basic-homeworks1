package ru.otus.java.basic.homeworks.hw24.processors;

import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;


public class CalculatorRequestProcessor implements RequestProcessor {
    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            int a = Integer.parseInt(request.getParameter("a"));
            int b = Integer.parseInt(request.getParameter("b"));
            String result = a + " + " + b + " = " + (a + b);

            HttpResponse response = new HttpResponse(200, "OK");
            response.setHeader("Content-Type", "text/html");
            response.setBody("<html><body><h1>" + result + "</h1></body></html>");
            return response;
        } catch (NumberFormatException | NullPointerException e) {
            HttpResponse response = new HttpResponse(400, "Bad Request");
            response.setHeader("Content-Type", "text/html");
            response.setBody("<html><body><h1>Invalid parameters</h1></body></html>");
            return response;
        }
    }
}