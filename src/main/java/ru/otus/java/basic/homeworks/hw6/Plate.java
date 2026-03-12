package ru.otus.java.basic.homeworks.hw6;

public class Plate {
    private int maxFood;
    private int currentFood;

    public Plate(int volume) {
        this.maxFood = volume;
        this.currentFood = volume;
    }

    public void addFood(int amount) {
        if (amount <= 0) {
            return;
        }
        if (currentFood + amount <= maxFood) {
            currentFood += amount;
        } else {
            currentFood = maxFood;
        }
        System.out.println("Еда добавлена");
    }

    public boolean decreaseFood(int amount) {
        if (amount > 0 && currentFood - amount >= 0) {
            currentFood -= amount;
            return true;
        }
        return false;
    }
    // Геттеры
    public int getMaxFood() {
        return maxFood;
    }

    public int getCurrentFood() {
        return currentFood;
    }
}
