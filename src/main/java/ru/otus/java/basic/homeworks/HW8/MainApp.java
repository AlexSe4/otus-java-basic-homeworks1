package ru.otus.java.basic.homeworks.HW8;

class MainApp {
    public static void main(String[] args) {
        String[][] correctArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}
        };


        String[][] wrongSizeArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
        };


        String[][] wrongDataArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "abc", "16"},
        };


        try {
            int result = SumArray.sumArray(correctArray);
            System.out.println("Сумма элементов корректного массива: " + result);
        } catch (AppArraySizeException | AppArrayDataException e) {
            System.out.println("Ошибка при обработке корректного массива:" + e.getMessage());
        }


        try {
            int result = SumArray.sumArray(wrongSizeArray);
            System.out.println("Сумма элементов массива с неправильным размером: " + result);
        } catch (AppArraySizeException | AppArrayDataException e) {
            System.out.println("Ошибка при обработке массива  с неправильным размером:" + e.getMessage());
        }

        try {
            int result = SumArray.sumArray(wrongDataArray);
            System.out.println("Сумма элементов массива с неправильными данными: " + result);
        } catch (AppArraySizeException | AppArrayDataException e) {
            System.out.println("Ошибка при обработке массива  с неправильными данными:" + e.getMessage());
        }
    }
}



