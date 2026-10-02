package hw.task1;

public class CounterTask implements Runnable {

    private final int number;              // numer TEGO wątku – przekazany w konstruktorze
    private static int started;

    public CounterTask(int number) {
        this.number = number;
    }

    static void main() throws InterruptedException {
        // TODO: utwórz N wątków, uruchom je, poczekaj na wszystkie, wypisz komunikat końcowy
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new CounterTask(i), "thread-" + i);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Wątki zakończyły działanie");
        System.out.println("started = " + started);
    }

    @Override
    public void run() throws RuntimeException {
        // TODO: wypisz numer, potem pętla 1..10 z Thread.sleep(100)
        System.out.printf("Wątek %d start!%n", this.number);
        for (int i = 1; i <= 10; i++) {
            System.out.printf("Wątek %d - %d%n", this.number, i);
            incrementStarted();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static synchronized void incrementStarted(){
        for (int j = 0; j < 100_000; j++) {
            started++;
        }
    }
}