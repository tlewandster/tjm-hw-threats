package watki.ch08;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** "Sprawdź, potem zrób" nie jest atomowe — nawet na polu atomowym. */
public class CheckThenAct {

    static final AtomicInteger LOADS = new AtomicInteger();

    /** Kosztowne wczytanie cennika — w prawdziwym kodzie: plik albo baza. */
    static String loadPriceList() {
        LOADS.incrementAndGet();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "price list";
    }

    public static void main(String[] args) throws InterruptedException {
        // ❌ dwie operacje: sprawdź, że pusto — i dopiero potem zapisz
        AtomicReference<String> broken = new AtomicReference<>();
        LOADS.set(0);
        runOnEightThreads(() -> {
            if (broken.get() == null) {
                broken.set(loadPriceList());
            }
        });
        System.out.println("get + set        -> loaded " + LOADS.get() + " times");

        // ✅ jedna niepodzielna operacja
        ConcurrentHashMap<String, String> fixed = new ConcurrentHashMap<>();
        LOADS.set(0);
        runOnEightThreads(() -> fixed.computeIfAbsent("prices", key -> loadPriceList()));
        System.out.println("computeIfAbsent  -> loaded " + LOADS.get() + " times");
    }

    static void runOnEightThreads(Runnable task) throws InterruptedException {
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
}
