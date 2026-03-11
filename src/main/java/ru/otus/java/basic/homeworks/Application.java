package ru.otus.java.basic.homeworks;
import java.util.Arrays;


public class Application {
    public static void main(String[] args) {
        int[] testArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        printTheString(5, "Привет!");
        sum(1, 2, 3, 4, 5, 6, 7, 8, 9);
        certainValue(testArr, 5);
        System.out.println("Массив после заполнения: " + Arrays.toString(testArr));
        plusValue(testArr, 1);
        System.out.println(Arrays.toString(testArr));
        sumComparison(testArr);
        int[] a = {1, 2, 3};
        int[] b = {2, 2};
        int[] c = {1, 1, 1, 1, 1};
        int[] sum = sumArrays(a, b, c);

        hasEquilibriumPoint(testArr);
        isDescending(testArr);
        int[] test1 = {5, 4, 3, 2, 1};
        reverse(testArr);

    }

    //Реализуйте метод, принимающий в качестве аргументов целое число и строку,
    //и печатающий в консоль строку указанное количество раз

    public static void printTheString(int n, String text) {;
        for (int i = 0; i < n; i++) {
            System.out.println(text);
        }
    }

    //Реализуйте метод, принимающий в качестве аргумента целочисленный массив,
    // суммирующий все элементы, значение которых больше 5, и печатающий полученную сумму в консоль.
    public static void sum(int... array) {
        if (array == null) {
            System.out.println("Ошибка: Массив не может быть null");
            return;
        }
        int sum = 0;
        for (int value : array) {
            if (value > 5) {
                sum += value;
            }
        }
        System.out.println(sum);
    }

    //Реализуйте метод, принимающий в качестве аргументов целое число и ссылку на целочисленный массив,
    // метод должен заполниться каждую ячейку массива указанным числом.

    public static void certainValue(int[] testArr, int num) {
        if (testArr == null) {
            System.out.println("Ошибка: Массив не может быть null");
            return;
        }
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] = num;
        }
    }

    //Реализуйте метод, принимающий в качестве аргументов целое число и ссылку на целочисленный массив,
    // увеличивающий каждый элемент которого на указанное число.
    public static void plusValue(int[] testArr, int num) {
        if (testArr == null) {
            System.out.println("Ошибка: Массив не может быть null");
            return;
        }
        for (int i = 0; i < testArr.length; i++) {
            testArr[i] += num;
        }
    }


    //Реализуйте метод, принимающий в качестве аргумента целочисленный массив, и печатающий в консоль
    // сумма элементов какой из половин массива больше.

    public static void sumComparison(int[] arr) {
        if (arr == null) {
            System.out.println("Ошибка: Массив не может быть null");
            return;
        }
        int sumleft = 0;
        int sumright = 0;
        int middle = arr.length / 2;

        System.out.println("Массив: " + Arrays.toString(arr));
        System.out.print("Левая половина:  ");

        for (int i = 0; i < middle; i++) {
            sumleft += arr[i];
            System.out.print(arr[i] + "  ");
            System.out.println("сумма:"   + sumleft + ")");
            System.out.print("Правая половина:   ");

        }
        for (int i = middle; i < arr.length; i++) {
            sumright += arr[i];
            System.out.println("(сумма:   "   + sumright + ")");

            if (arr.length % 2 != 0) {
                System.out.println("→ При нечетной длине центральный элемент [" + arr[middle] + "] включен в правую половину");
            }

            if (sumleft > sumright) {
                System.out.println("Левая половина больше (разница: " + (sumleft - sumright) + ")");
            } else if (sumleft < sumright) {
                System.out.println("Правая половина больше (разница: " + (sumright - sumleft) + ")");
            } else {
                System.out.println("Суммы половин равны");
            }
            System.out.println();
        }
    }
    //Реализуйте метод, принимающий на вход набор целочисленных массивов, и получающий новый массив равный сумме входящих;
    public static int[] sumArrays(int[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return new int[0];
        }
        int maxLength = 0;
        for (int[] arr : arrays) {
            if (arr != null && arr.length > maxLength) {
                maxLength = arr.length;
            }
        }
        int[] result = new int[maxLength];
        for (int i = 0; i < maxLength; i++) {
            int sum = 0;
            for (int[] arr : arrays) {
                if (arr != null && i < arr.length) {
                    sum += arr[i];
                }
            }
            result[i] = sum;
            System.out.println(Arrays.toString(result));
        }

        return result;
    }
    //Реализуйте метод, проверяющий что есть точка в массиве, в которой сумма левой и правой части равны. Точка находится между элементами.
    public static boolean hasEquilibriumPoint(int[] array) {
        if (array == null || array.length < 2) {
            return false;
        }

        int total = 0;
        for (int num : array) {
            total += num;
        }


        int leftSum = 0;
        for (int i = 0; i < array.length - 1; i++) {
            leftSum += array[i]; // добавляем текущий элемент к левой сумме
            // Проверяем условие: сумма слева равна сумме справа
            // Сумма справа = общая сумма - сумма слева
            if (leftSum == total - leftSum) {
                return true;
            }
        }

        return false;
    }

//Реализуйте метод, проверяющий что все элементы массива идут в порядке убывания
public static boolean isDescending(int[] array) {
    if (array == null || array.length <= 1) {
        return true;
    }
    for (int i = 0; i < array.length - 1; i++) {
        if (array[i] <= array[i + 1]) {
            return false;
        }
    }
    return true;
}

    //Реализуйте метод, переворачивающий входящий массив
    private static void reverse(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int tmp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmp;
        }
        System.out.println(Arrays.toString(arr));

    }
}


