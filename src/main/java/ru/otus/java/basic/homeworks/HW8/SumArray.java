package ru.otus.java.basic.homeworks.HW8;

class AppArraySizeException extends Exception {
    public AppArraySizeException(String message) {
        super(message);
    }
}

class AppArrayDataException extends Exception {
    public AppArrayDataException(String message) {
        super(message);
    }
}

public class SumArray {
    public static int sumArray(String[][] array) throws AppArraySizeException, AppArrayDataException {
        if (array == null) {
            throw new AppArraySizeException("Массив не должен быть null");
        }
        if (array.length != 4) {
            throw new AppArraySizeException("Массив должен быть 4x4.Некорректный размер  " +  array.length);
        }
        for (int i = 0; i < array.length; i++){
            if (array[i] == null) {
                throw new AppArraySizeException("Строка" + i + "массива равна null.");
            }
            if (array[i].length !=4) {
                throw new AppArraySizeException("Некорректный размер массива: строка" + i +"должна содержать 4 колонки, получено   " + array[i].length);
            }
        }
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                try {
                    int number = Integer.parseInt(array[i][j]);
                    sum += number;
                } catch (NumberFormatException e) {
                    throw new AppArrayDataException("В ячейке неверные данные [" + i + "][" + j + "]: " + array[i][j]);
                }
            }
        }
        return sum;
    }
}
