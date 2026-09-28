package watki.ch07;

public class OrderCounter {
    private final Object lock = new Object();
    private int accepted;

    static void main() throws InterruptedException {
        OrderCounter counter = new OrderCounter();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() ->
            {
                for (int j = 0; j < 100_000; j++) {
                    counter.accept();
                }
            }
            );
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(counter.count());
    }

    private int count() {
//        synchronized (lock) {
            return accepted;
//        }
    }

    private void accept() {
        synchronized (lock) {
            accepted++;
        }
    }
}
