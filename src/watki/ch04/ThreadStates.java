package watki.ch04;

/** Cykl życia wątku podejrzany metodą getState(). */
public class ThreadStates {

    static void main() throws InterruptedException {
        Object lock = new Object();

        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(300);          // TIMED_WAITING
                synchronized (lock) {
                    lock.wait();            // WAITING
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "worker");

        System.out.println("after new     -> " + worker.getState());
        worker.start();
        System.out.println("after start   -> " + worker.getState());

        Thread.sleep(100);
        System.out.println("during sleep  -> " + worker.getState());

        Thread.sleep(400);
        System.out.println("during wait   -> " + worker.getState());

        synchronized (lock) {
            lock.notifyAll();               // budzimy wątek
        }
        worker.join();
        System.out.println("after join    -> " + worker.getState());
    }
}
