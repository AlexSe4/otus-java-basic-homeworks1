package ru.otus.java.basic.homeworks.hw19;
import java.util.ArrayList;
class Box<T extends Fruit> {
    private ArrayList<T> fruits;
    private String type;

    public Box(String type) {
        this.fruits = new ArrayList<>();
        this.type = type;
    }

    public void addFruit(T fruit) {
        fruits.add(fruit);
        System.out.println("Добавлен " + fruit.getClass().getSimpleName() + " в коробку " + type);
    }

    public float getWeight() {
        float totalWeight = 0;
        for (T fruit : fruits) {
            totalWeight += fruit.getWeight();
        }
        return totalWeight;
    }

    public boolean compare(Box<?> otherBox) {
        return Math.abs(this.getWeight() - otherBox.getWeight()) < 0.0001f;
    }

    public void pourInto(Box<T> otherBox) {
        if (this == otherBox) {
            System.out.println("Нельзя пересыпать в ту же коробку!");
            return;
        }

        for (T fruit : fruits) {
            otherBox.addFruit(fruit);
        }

        fruits.clear();
        System.out.println("Все фрукты пересыпаны из коробки " + type + " в другую коробку");
    }

    public int getCount() {
        return fruits.size();
    }
}
