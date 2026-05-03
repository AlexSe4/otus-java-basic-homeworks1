package ru.otus.java.basic.homeworks.hw24;

import ru.otus.java.basic.homeworks.hw24.processors.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundRequestProcessor;

    public Dispatcher() {
        this.defaultNotFoundRequestProcessor = new DefaultNotFoundRequestProcessor();
        this.processors = new HashMap<>();
        this.processors.put("GET /calculator", new CalculatorRequestProcessor());
        this.processors.put("GET /hello", new HelloRequestProcessor());
        this.processors.put("GET /items", new GetItemsRequestProcessor());
        this.processors.put("POST /items", new CreateItemRequestProcessor());
    }

    public HttpResponse execute(HttpRequest request) throws IOException {
        RequestProcessor processor = processors.getOrDefault(
                request.getRoutingKey(), defaultNotFoundRequestProcessor);
        return processor.execute(request);
    }
}