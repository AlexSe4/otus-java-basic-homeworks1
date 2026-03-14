package ru.otus.java.basic.homeworks.hw77;

public class MainApp {
    public static void main(String[] args) {
        Person person = new Person("Ив");

        Car car = new Car(50.0);
        Horse horse = new Horse();
        Bicycle bicycle = new Bicycle();
        ATV atv = new ATV();

        car.getInfo();
        horse.getInfo();
        bicycle.getInfo();
        atv.getInfo();

        System.out.println("Пешком");
        person.move(10, Terrain.PLAIN);
        person.move(5, Terrain.DENSE_FOREST);
        person.move(3, Terrain.MARSH);

        System.out.println("На машине");
        person.sitOn(car);
        person.move(50, Terrain.PLAIN);
        person.move(10, Terrain.DENSE_FOREST);
        person.move(5, Terrain.MARSH);
        person.standUp();

        System.out.println("На лошади");
        person.sitOn(horse);
        person.move(30, Terrain.PLAIN);
        person.move(20, Terrain.DENSE_FOREST);
        person.move(5, Terrain.MARSH);
        person.standUp();

        System.out.println("На велосипеде");
        person.sitOn(bicycle);
        person.move(40, Terrain.PLAIN);
        person.move(20, Terrain.DENSE_FOREST);
        person.move(5, Terrain.MARSH);
        person.standUp();

        System.out.println("На вездеходе");
        person.sitOn(atv);
        person.move(50, Terrain.PLAIN);
        person.move(30, Terrain.DENSE_FOREST);
        person.move(20, Terrain.MARSH);
        person.standUp();
    }
}

