package ru.otus.java.basic.homeworks.HW3;

public class App {
    public static void main(String[] args) {
        System.out.println(sumOfPositiveElements(new int[][] {{1, -2, 3}, {4, 0, 6}, {7, 8, -9}}));
        printSquare(5);
        zeroingDiagonals(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        System.out.println(findMax(new int[][]{{-10, 2, 3}, {4, 0, 6}, {7, 8, 9}}));
        System.out.println(sumOfTheSecondLine(new int[][]{{-1, 1, 1, 1}, {1, 2, 3, 1}, {4, 5, 6, 1}, {1, 1, -1, 1}}));
    }

//Реализовать метод sumOfPositiveElements(..), принимающий
//в качестве аргумента целочисленный двумерный массив,
//метод должен посчитать и вернуть сумму всех элементов массива, которые больше 0;

    public static int sumOfPositiveElements(int[][] array) {
        int sum = 0;
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    for (int j = 0; j < array[i].length; j++) {
                        if (array[i][j] > 0) {
                            sum += array[i][j];
                        }
                    }
                }
            }
        }
        return sum;
    }

//Реализовать метод, который принимает в качестве аргумента int size и печатает
//в консоль квадрат из символов * со сторонами соответствующей длины;
    public static void printSquare(int size) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == 0 || y == 0 || x == size - 1 || y == size - 1) {
                    System.out.print("*  ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

//Реализовать метод, принимающий в качестве аргумента двумерный
//целочисленный массив, и зануляющий его диагональные элементы
    public static void zeroingDiagonals(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (i == j || i + j == array.length - 1) {
                    array[i][j] = 0;
                }
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

// Реализовать метод findMax(int[][] array)
// который должен найти и вернуть максимальный элемент массива;
    public static int findMax(int[][] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Массив не должен быть пустым");
        }
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] > max) {
                    max = array[i][j];
                }
            }
        }

        return max;
    }

// Реализуйте метод, который считает сумму элементов второй строки двумерного массива,
// если второй строки не существует, то в качестве результата необходимо вернуть -1
    public static int sumOfTheSecondLine(int[][] array) {
        if (array.length < 2) {
            return -1;
        }

        int sum = 0;
        for (int j = 0; j < array[1].length; j++) {
            sum += array[1][j];
        }
        return sum;
    }
}
