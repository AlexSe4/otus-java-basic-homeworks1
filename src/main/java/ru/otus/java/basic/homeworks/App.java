package ru.otus.java.basic.homeworks;

import java.util.Random;
import java.util.Scanner;

public class App{

    // Метод greetings()
    public static void greetings() {
        System.out.println("Hello");
        System.out.println("World");
        System.out.println("from");
        System.out.println("Java");
    }

    // Метод checkSign()
    public static void checkSign(int a, int b, int c) {
        int sum = a + b + c;
        if (sum >= 0) {
            System.out.println("Сумма положительная");
        } else {
            System.out.println("Сумма отрицательная");
        }
    }

    // метод selectColor()
    public static void selectColor() {
        int data = new Random().nextInt(100) + 1;
        if (data <= 10) {
            System.out.println("Красный");
        } else if (data <= 20) {
            System.out.println("Желтый");
        } else {
            System.out.println("Зеленый");
        }
    }
    // Метод compareNumbers()
    public static void compareNumbers() {
        int a = new Random().nextInt(100) + 1;
        int b = new Random().nextInt(100) + 1;
        if (a >= b) {
            System.out.println("a >= b");
        } else {
            System.out.println("a < b");
        }
    }

    //Метод addOrSubtractAndPrint()
    public static void addOrSubtractAndPrint(int initValue, int delta, boolean increment) {
        if (increment == true) {
            System.out.println(initValue + delta + " - результат сложения");
        } else {
            System.out.println(initValue - delta + " - результат вычитания");

        }
    }
    public static void main(String[] args) {
        greetings();
        checkSign(5, 7, 9);
        selectColor();
        compareNumbers();
        addOrSubtractAndPrint(4, 7, false);


        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите число от 1 до 5, чтобы выбрать метод: ");
        int inputNumber;
        if (scanner.hasNext()) {
            inputNumber = scanner.nextInt();
        } else  {
            System.out.println("Ошибка. Нужно ввести число!");
            scanner.close();
            return;
        }
        Random rand = new Random();
        int a = rand.nextInt(100) + 1, b = rand.nextInt(100) + 1,
                c = rand.nextInt(100) + 1, initValue = rand.nextInt(100) + 1,
                delta = rand.nextInt(100) + 1;
        boolean increment = rand.nextBoolean();

        switch (inputNumber) {
            case 1:
                greetings();
                break;
            case 2:
                checkSign(a, b, c);
                break;
            case 3:
                selectColor();
                break;
            case 4:
                compareNumbers();
                break;
            case 5:
                addOrSubtractAndPrint(initValue, delta, increment);
                break;
            default:
                System.out.println("Ошибка! Число должно быть от 1 до 5!");

        }
    }
}
