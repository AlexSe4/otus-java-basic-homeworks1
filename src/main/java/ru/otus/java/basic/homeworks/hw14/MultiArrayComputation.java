package ru.otus.java.basic.homeworks.hw14;

public class MultiArrayComputation {
    private static final int ARRAY_SIZE = 100_000_000;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        double[] array = new double[ARRAY_SIZE];
        long startTime = System.nanoTime();

        Thread[] threads = new Thread[NUM_THREADS];
        int chunkSize = ARRAY_SIZE / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            final int start = i * chunkSize;
            final int end = (i == NUM_THREADS - 1) ? ARRAY_SIZE : (i + 1) * chunkSize;

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = start; j < end; j++) {
                        array[j] = 1.14 * Math.cos(j) * Math.sin(j * 0.2) * Math.cos(j / 1.2);
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Ожидание потока было прервано: " + e.getMessage());
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Время выполнения с 4 потоками: " + durationMs + " мc");
    }
}

