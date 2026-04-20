package ru.otus.java.basic.homeworks.hw20;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class SearchCounter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя файла: ");
        String fileName = scanner.nextLine();

        System.out.print("Введите искомую последовательность символов: ");
        String searchString = scanner.nextLine();

        try {
            int count = countOccurrences(fileName, searchString);

            System.out.printf("%n Результат \"%s \" + встречается %d раз(а) %n", searchString, count);
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        scanner.close();
    }

    public static int countOccurrences(String fileName, String searchString)
            throws IOException {

        if (searchString == null || searchString.isEmpty()) {
            return 0;
        }

        int count = 0;
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        String fileContent = content.toString();
        int index = 0;
        while ((index = fileContent.indexOf(searchString, index)) != -1) {
            count++;
            index += searchString.length();
        }

        return count;
    }
}

