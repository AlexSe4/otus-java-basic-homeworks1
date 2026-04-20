package ru.otus.java.basic.homeworks.hw21;
import java.util.concurrent.*;

public class ThreadCycle {
    private static final Object lock = new Object();
    private static int count = 0;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
            executor.submit(() -> printLetter('A', 5));
            executor.submit(() -> printLetter('B', 5));
            executor.submit(() -> printLetter('C', 5));
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void printLetter(char letter, int times) {
        for (int i = 0; i < times; i++) {
            synchronized (lock) {
                while (count % 3 != getLetterIndex(letter)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.print(letter);
                count++;
                lock.notifyAll();
            }
        }
    }

    private static int getLetterIndex(char letter) {
        return switch (letter) {
            case 'A' -> 0;
            case 'B' -> 1;
            case 'C' -> 2;
            default -> throw new IllegalArgumentException("Invalid letter: " + letter);
        };
    }
}
