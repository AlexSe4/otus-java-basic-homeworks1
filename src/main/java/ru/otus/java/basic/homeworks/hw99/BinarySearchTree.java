package ru.otus.java.basic.homeworks.hw99;

import java.util.*;


interface SearchTree <T> {
    T find(T element);
    List<T> getSortedList();
}

public class BinarySearchTree
   implements SearchTree<Integer> {
    private TreeNode root;

    private static class TreeNode {
        Integer value;
        TreeNode left;
        TreeNode right;

        TreeNode(Integer value) {
            this.value = value;
        }
    }

    public BinarySearchTree(List<Integer> elements) {
        for (Integer element : elements) {
            insert(element);
        }
    }

    private void insert(Integer value) {
        root = insertRec(root, value);
    }

    private TreeNode insertRec(TreeNode node, Integer value) {
        if (node == null) {
            return new TreeNode(value);
        }
        if (value < node.value) {
            node.left = insertRec(node.left, value);
        } else if (value > node.value) {
            node.right = insertRec(node.right, value);
        }
        return node;
    }

    @Override
    public Integer find(Integer element) {
        return findRec(root, element);
    }

    private Integer findRec(TreeNode node, Integer value) {
        if (node == null) {
            return null;
        }
        if (value.equals(node.value)) {
            return node.value;
        }
        return value < node.value ? findRec(node.left, value) : findRec(node.right, value);
    }

    @Override
    public List<Integer> getSortedList() {
        List<Integer> sortedList = new ArrayList<>();
        inOrderTraversal(root, sortedList);
        return sortedList;
    }

    private void inOrderTraversal(TreeNode node, List<Integer> sortedList) {
        if (node != null) {
            inOrderTraversal(node.left, sortedList);
            sortedList.add(node.value);
            inOrderTraversal(node.right, sortedList);
        }
    }
}






