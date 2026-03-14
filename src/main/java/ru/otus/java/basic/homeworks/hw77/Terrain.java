package ru.otus.java.basic.homeworks.hw77;

public enum Terrain {
    DENSE_FOREST("густой лес"),
    PLAIN("равнина"),
    MARSH("болото");

    private final String title;
    Terrain(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}