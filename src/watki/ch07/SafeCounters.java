package watki.ch07;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Trzy poprawne liczniki: synchronized, AtomicInteger i LongAdder.
 */
public class SafeCounters {

    private static final Object LOCK = new Object();
    private static final AtomicInteger ATOMIC = new AtomicInteger();
    private static final LongAdder ADDER = new LongAdder();
    private static int guarded = 0;

    public static void main(String[] args) throws InterruptedException {
        runOnFourThreads(() -> {
            synchronized (LOCK) {       // sekcja krytyczna: jeden wątek naraz
                guarded++;
            }
        });
        runOnFourThreads(ATOMIC::incrementAndGet);
        runOnFourThreads(ADDER::increment);

        System.out.println("synchronized : " + guarded);
        System.out.println("AtomicInteger: " + ATOMIC.get());
        System.out.println("LongAdder    : " + ADDER.sum());
    }

    /**
     * Uruchamia operację 4 x 500 000 razy na czterech wątkach i czeka na koniec.
     */
    static void runOnFourThreads(Runnable operation) throws InterruptedException {
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 500_000; j++) {
                    operation.run();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}
