package ru.otus.java.basic.homeworks.homework1;

import java.util.Random;
import java.util.Scanner;

public class App {
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
        int data = 14;
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

        int a = 10;
        int b = 7;
        if (a >= b) {
            System.out.println("a >= b");
        } else {
            System.out.println("a < b");
        }
    }
//Метод addOrSubtractAndPrint()
    public static void addOrSubtractAndPrint(int initValue, int delta, boolean increment) {
        if (increment) {
            int result = initValue + delta;
            System.out.println(initValue + "+" + delta + "=" + result);
        } else {
            int result = initValue - delta;
            System.out.println(initValue + "-" + delta + "=" + result);

        }
    }

    // Метод из (*) части
    public static void executeMethod(int methodNumber) {
        Random random = new Random();

        switch (methodNumber) {
            case 1:
                greetings();
                break;
            case 2:
                int a = random.nextInt(100) - 50;
                int b = random.nextInt(100) - 50;
                int c = random.nextInt(100) - 50;
                System.out.println("a = " + a + ", b = " + b + ", c = " + c);
                checkSign(a, b, c);
                break;
            case 3:
                selectColor();
                break;
            case 4:
                compareNumbers();
                break;
            case 5:
                int initValue = random.nextInt(100);
                int delta = random.nextInt(50);
                boolean increment = random.nextBoolean();
                System.out.println("initValue = " + initValue + ", delta = " + delta + ", increment = " + increment);
                addOrSubtractAndPrint(initValue, delta, increment);
                break;
            default:
                System.out.println("Неверный номер метода");
        }
    }


    public static void main(String[] args) {
        System.out.println("Вызов метода greetings()");
        greetings();
        System.out.println("Вызов метода checkSign()");
        checkSign(1, 2, 3);
        checkSign(-10, -5, -2);
        System.out.println("Вызов метода selectColor()");
        selectColor();
        System.out.println("Вызов метода compareNumbers()");
        compareNumbers();
        System.out.println("Вызов addOrSubtractAndPrint()");
        addOrSubtractAndPrint(100, 25, true);
        addOrSubtractAndPrint(100, 25, false);
            // (*) Дополнительная часть
            System.out.println("Звездочка");
            Scanner scanner = new Scanner(System.in);

            System.out.println("Введите число от 1 до 5:");
            int methodNumber = scanner.nextInt();

            if (methodNumber >= 1 && methodNumber <= 5) {
                executeMethod(methodNumber);
            } else {
                System.out.println("Число должно быть от 1 до 5");
            }

            scanner.close();
        }

    }








