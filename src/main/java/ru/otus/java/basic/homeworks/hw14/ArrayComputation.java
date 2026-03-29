package ru.otus.java.basic.homeworks.hw14;

public class ArrayComputation {
    private static final int ARRAY_SIZE = 100_000_000;

    public static void main(String[] args) {
        double[] array = new double[ARRAY_SIZE];
        long startTime = System.nanoTime();

        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = 1.14 * Math.cos(i) * Math.sin(i * 0.2) * Math.cos(i / 1.2);
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Время выполнения: " + durationMs + " мc");
    }
}

