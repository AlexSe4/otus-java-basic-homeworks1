package ru.otus.java.basic.homeworks.hw5;

public class MainApp {
    public static void main(String[] args) {
        Cat cat = new Cat("Буся", 5, 0, 20);
        Dog dog = new Dog("Арчи", 12, 4, 50);
        Horse horse = new Horse("Ворон", 15, 3, 100);

        cat.run(5);
        cat.swim(5);

        dog.run(10);
        dog.swim(15);

        horse.run(30);
        horse.swim(30);

        System.out.println("Состояние животных после попыток:");
        cat.info();
        System.out.println();
        dog.info();
        System.out.println();
        horse.info();
    }
}
