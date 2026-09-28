package watki.ch06;

/** Bez volatile wątek roboczy może NIGDY nie zobaczyć zmiany flagi. */
public class VisibilityBug {

    static volatile boolean running = true;      // ← brakuje volatile

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (running) {           // JIT może wciągnąć odczyt do rejestru
                spins++;
            }
            System.out.println("worker stopped after " + spins + " spins");
        }, "worker");

        worker.start();
        Thread.sleep(500);
        running = false;
        System.out.println("main: flag set to false, waiting for worker...");

        worker.join(3000);              // czekamy maksymalnie 3 sekundy
        if (worker.isAlive()) {
            System.out.println("main: worker IS STILL RUNNING - visibility bug!");
            System.exit(1);             // ubijamy zawieszony program
        }
    }
}
