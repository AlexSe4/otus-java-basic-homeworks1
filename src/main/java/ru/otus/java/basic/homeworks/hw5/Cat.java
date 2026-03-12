package ru.otus.java.basic.homeworks.hw5;


public class Cat extends Animal {
    public Cat(String name, double runSpeed, double swimSpeed, double stamina) {
        super(name, runSpeed, swimSpeed, stamina);
    }
    // кот не умеет плавать
    @Override
    public double swim(int distance) {
        System.out.println(name + " не умеет плавать.");
        return -1;
    }
}

