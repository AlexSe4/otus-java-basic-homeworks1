package ru.otus.java.basic.homeworks.hw77;

public interface Transport {
    boolean move(double distance, Terrain terrain);
    void setDriver(Person person);
    String getTransportName();
}