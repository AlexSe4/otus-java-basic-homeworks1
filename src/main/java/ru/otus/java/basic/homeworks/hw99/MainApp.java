package ru.otus.java.basic.homeworks.hw99;

import java.util.Arrays;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        List<Integer> elements = Arrays.asList(5, 3, 7, 2, 4, 6, 8);
        BinarySearchTree bst = new BinarySearchTree(elements);

        Integer searchElement = 2;
        Integer result = bst.find(searchElement);
        if (result != null) {
            System.out.println("Элемент (" + searchElement + ") найден!");
        } else {
            System.out.println("Элемент (" + searchElement + ") не найден!");
        }

        List<Integer> sortedList = bst.getSortedList();
        System.out.println("Cписок: " + sortedList);
    }
}
