package watki.ch06;

/** Jedno słowo — volatile — i wątek widzi zmianę flagi natychmiast. */
public class VolatileFlag {

    static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (running) {
                spins++;
            }
            System.out.println("worker stopped, spins > 0 = " + (spins > 0));
        }, "worker");

        worker.start();
        Thread.sleep(500);
        running = false;
        System.out.println("main: flag set to false, waiting for worker...");

        worker.join(3000);
        System.out.println("main: worker alive = " + worker.isAlive());
    }
}
