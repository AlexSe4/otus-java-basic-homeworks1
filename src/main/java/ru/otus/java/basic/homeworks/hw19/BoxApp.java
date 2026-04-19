package ru.otus.java.basic.homeworks.hw19;

public class BoxApp {
    public static void main(String[] args) {
        Box<Apple> appleBox = new Box<>("Коробка c яблоками");
        Box<Orange> orangeBox = new Box<>("Коробка c апельсинами");
        Box<Fruit> mixedBox = new Box<>("Коробка с фруктами");


        appleBox.addFruit(new Apple());
        appleBox.addFruit(new Apple());
        appleBox.addFruit(new Apple());

        orangeBox.addFruit(new Orange());
        orangeBox.addFruit(new Orange());

        mixedBox.addFruit(new Apple());
        mixedBox.addFruit(new Orange());


        System.out.println("\nВес коробки c яблоками: " + appleBox.getWeight());
        System.out.println("Вес коробки с апельсинами: " + orangeBox.getWeight());
        System.out.println("Вес коробки с фруктами: " + mixedBox.getWeight());


        System.out.println("\nЯблочная и апельсиновая коробки равны по весу? " + appleBox.compare(orangeBox));
        System.out.println("Яблочная и смешанная коробки равны? " + appleBox.compare(mixedBox));


        Box<Apple> anotherAppleBox = new Box<>("Другая коробка с яблоками");
        appleBox.pourInto(anotherAppleBox);

        System.out.println("\nПосле пересыпания:");
        System.out.println("В первой коробке с яблоками: " + appleBox.getCount());
        System.out.println("Во второй коробке с яблоками: " + anotherAppleBox.getCount());
    }
}
