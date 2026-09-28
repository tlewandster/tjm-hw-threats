package hw.task2;

import java.util.Arrays;

public class ParallelSum {

    private static final int SIZE = 1_000_000;
    private static final int PARTS = 4;

    static long sequentialSum(int[] data) {
        return Arrays.stream(data)
                .asLongStream()
                .sum();
    }

    public static void main(String[] args) throws InterruptedException {
        // TODO: wypełnij tablicę, podziel na PARTS, uruchom wątki, poczekaj, zsumuj, porównaj
        int[] data = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            data[i] = (int) (Math.random() * 10);
        }

        int start = 0;
        int step = SIZE / PARTS;
        int rest = SIZE % PARTS;

        Thread[] threads = new Thread[PARTS];

        for (int i = 0; i < PARTS; i++) {
            int from = start;
            int to = from + step;
            if (i == PARTS - 1) to += rest;
            threads[i] = new Thread(new PartialSumTask(data, from, to, i));
            start = to;
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        long parallelSum = Arrays.stream(PartialSumTask.results).sum();
        long sequentialSum = sequentialSum(data);
        System.out.println(parallelSum + (parallelSum == sequentialSum ? " = " : " != ") + sequentialSum);
    }

    /**
     * Zadanie liczące sumę fragmentu [from, to) i zapisujące ją pod swój indeks.
     */
    static class PartialSumTask implements Runnable {
        private static final long[] results = new long[PARTS];       // WSPÓLNA tablica wyników
        private final int[] data;
        private final int from;             // włącznie
        private final int to;               // wyłącznie
        private final int index;            // MÓJ i tylko mój indeks w results

        // TODO: konstruktor
        public PartialSumTask(int[] data, int from, int to, int index) {
            this.data = data;
            this.from = from;
            this.to = to;
            this.index = index;
        }


        @Override
        public void run() {
            results[index] = Arrays.stream(Arrays.copyOfRange(data, from, to))
                    .asLongStream()
                    .sum();
        }
    }
}