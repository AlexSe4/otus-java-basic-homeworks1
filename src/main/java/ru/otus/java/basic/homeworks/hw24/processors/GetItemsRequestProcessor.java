package ru.otus.java.basic.homeworks.hw24.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;
import ru.otus.java.basic.homeworks.hw24.app.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetItemsRequestProcessor implements RequestProcessor {
    @Override
    public HttpResponse execute(HttpRequest request) {
        List<Item> items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Bread", 50),
                new Item(2L, "Milk", 150),
                new Item(3L, "Cheese", 400)
        ));
        String json = new Gson().toJson(items);

        HttpResponse response = new HttpResponse(200, "OK");
        response.setHeader("Content-Type", "application/json");
        response.setBody(json);
        return response;
    }
}

