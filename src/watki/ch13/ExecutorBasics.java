package watki.ch13;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Pula wątków: 6 zadań obsłużonych przez 2 stałe wątki. */
public class ExecutorBasics {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 1; i <= 6; i++) {
            int orderId = i;
            pool.submit(() -> {
                System.out.println("order " + orderId + " handled by "
                        + Thread.currentThread().getName());
                sleep(100);
            });
        }

        pool.shutdown();                                // nie przyjmuj nowych zadań
        boolean finished = pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("all tasks finished = " + finished);
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
