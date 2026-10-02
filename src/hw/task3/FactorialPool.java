package hw.task3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FactorialPool {

    private static final int POOL_SIZE = 4;
    private static final int MAX_N = 20;
    private static final int MIN_SLEEP_TIME_IN_MS = 100;
    private static final int MAX_SLEEP_TIME_IN_MS = 500;

    /**
     * Liczy n! i zwraca wynik; w środku losowy sleep 100–500 ms.
     */
    static BigInteger factorial(int n) throws InterruptedException {
        if (n <= 1) {
            randomSleep();
            return BigInteger.ONE;
        }
        return factorial(n - 1).multiply(BigInteger.valueOf(n));
    }

    private static void randomSleep() throws InterruptedException {
        int sleepDuration =
                MIN_SLEEP_TIME_IN_MS + (int) (Math.random() * ((MAX_SLEEP_TIME_IN_MS - MIN_SLEEP_TIME_IN_MS) + 1));
        Thread.sleep(sleepDuration);
    }

    static void main() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

        List<Callable<BigInteger>> tasks = new ArrayList<>();
        for (int i = 0; i < MAX_N; i++) {
            int nextN = i + 1;
            tasks.add(() -> factorial(nextN));
        }
        List<Future<BigInteger>> futures = pool.invokeAll(tasks);

        int nextN = 0;
        for (Future<BigInteger> future : futures) {
            System.out.printf("%d -> %s%n",++nextN,future.get().toString());
        }

        pool.shutdown();
    }
}