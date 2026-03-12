package ru.otus.java.basic.homeworks.hw6;

public class Cat {
    private String name;
    private int appetite;
    private boolean full;


    public Cat(String name, int appetite) {
        this.name = name;
        this.appetite = appetite;
        this.full = false;
    }


    public void eat(Plate plate) {
        if (plate.decreaseFood(appetite)) {
            full = true;
        }

    }

    // Геттеры
    public String getName() {
        return name;
    }

    public int getAppetite() {
        return appetite;
    }

    public boolean isFull() {
        return full;
    }
}