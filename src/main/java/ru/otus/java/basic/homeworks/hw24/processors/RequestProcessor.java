package ru.otus.java.basic.homeworks.hw24.processors;

import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.HttpResponse;

import java.io.IOException;

public interface RequestProcessor {
    HttpResponse execute(HttpRequest request) throws IOException;
}
