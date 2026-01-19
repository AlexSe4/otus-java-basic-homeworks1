package ru.otus.java.basic.homeworks;
import java.util.Random;
import java.util.Scanner;

public class Application {
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
        int a = new Random().nextInt(100) + 1;
        int b = new Random().nextInt(100) + 1;
        if (a >= b) {
            System.out.println("a >= b");
        } else {
            System.out.println("a < b");
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

        System.out.print("Введите число от 1 до 5, чтобы выбрать метод: ");
        Scanner keyboard = new Scanner(System.in);
        int inputNumber, a, b, c, initValue, delta;
        boolean increment;
        inputNumber = keyboard.nextInt();
        a = (int) (Math.random() * 20 - 10);
        b = (int) (Math.random() * 20 - 10);
        c = (int) (Math.random() * 20 - 10);
        initValue = (int) (Math.random() * 10);
        delta = (int) (Math.random() * 10);
        increment = new Random().nextBoolean();

        switch (inputNumber) {
            case 1:
                greetings();
                break;
            case 2:
                checkSign(a, b, c);
                break;
            case 3:
                compareNumbers();
                break;
            case 5:
                addOrSubtractAndPrint(initValue, delta, increment);
                break;
            default:
                System.out.println("Ошибка!");

        }
    }
}



