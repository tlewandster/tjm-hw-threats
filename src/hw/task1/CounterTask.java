package hw.task1;

public class CounterTask implements Runnable {

    private final int number;              // numer TEGO wątku – przekazany w konstruktorze

    public CounterTask(int number) {
        this.number = number;
    }

    public static void main(String[] args) throws InterruptedException {
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
    }

    @Override
    public void run() throws RuntimeException {
        // TODO: wypisz numer, potem pętla 1..10 z Thread.sleep(100)
        System.out.printf("Wątek %d start!%n", this.number);
        for (int i = 1; i <= 10; i++) {
            System.out.printf("Wątek %d - %d%n", this.number, i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}