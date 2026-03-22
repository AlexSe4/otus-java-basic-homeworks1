package ru.otus.java.basic.homeworks.hw12;

import java.io.*;
import java.util.Scanner;

public class FileApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя файла для работы: ");
        String fileName = scanner.nextLine();
        File file = new File(fileName);

        if (file.exists()) {
            System.out.println("Файл существует: " + fileName);
            System.out.println("Имя файла: " + file.getName());

            String parentDir = file.getParent();
            if (parentDir != null) {
                System.out.println("Родительский каталог: " + parentDir);
            } else {
                System.out.println("Родительский каталог отсутствует");
            }
        } else {
            System.out.println("Ошибка: Файл '" + fileName + "' не найден!");
            scanner.close();
            return;
        }

        System.out.println("\n=== Содержимое файла " + fileName + " Японская литература ===");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                System.out.println(lineNumber + ". " + line);
                lineNumber++;
            }
            if (lineNumber == 1) {
                System.out.println("(файл пуст)");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            scanner.close();
            return;
        }

        System.out.print("\nВведите строку для добавления в файл: ");
        String newLine = scanner.nextLine();

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(newLine + System.lineSeparator());
            System.out.println("\nСтрока успешно добавлена в файл '" + fileName + "'");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }

        scanner.close();
    }
}
