package watki.ch13;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Future = pokwitowanie na wynik, który jeszcze nie istnieje. */
public class FutureBasics {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // try-with-resources działa od Javy 19 — ExecutorService jest AutoCloseable
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {

            var total = pool.submit(() -> {         // Callable<Integer>
                Thread.sleep(400);
                return 149 * 3;
            });

            var failing = pool.submit(() -> {
                Thread.sleep(100);
                throw new IllegalStateException("price service is down");
            });

            System.out.println("is total ready right away? " + total.isDone());
            System.out.println("total = " + total.get());     // tu blokujemy
            System.out.println("is total ready now? " + total.isDone());

            try {
                failing.get();
            } catch (ExecutionException e) {
                // prawdziwy wyjątek zadania siedzi w getCause()
                System.out.println("task failed with: " + e.getCause());
            }
        }
    }
}
