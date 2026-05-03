package ru.otus.java.basic.homeworks.hw24.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;
import ru.otus.java.basic.homeworks.hw24.app.Item;


public class CreateItemRequestProcessor implements RequestProcessor {
    @Override
    public HttpResponse execute(HttpRequest request) {
        Gson gson = new Gson();
        try {
            Item item = gson.fromJson(request.getBody(), Item.class);
            System.out.println("Создан объект: " + item);

            HttpResponse response = new HttpResponse(201, "Created");
            response.setHeader("Content-Type", "application/json");
            response.setBody(gson.toJson(item)); // возвращаем созданный объект
            return response;
        } catch (Exception e) {
            HttpResponse response = new HttpResponse(400, "Bad Request");
            response.setHeader("Content-Type", "application/json");
            response.setBody("{\"error\": \"Invalid JSON format\"}");
            return response;
        }
    }
}

