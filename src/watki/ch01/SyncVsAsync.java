package watki.ch01;

/** Synchronicznie vs asynchronicznie — ten sam plan pracy, dwa czasy. */
public class SyncVsAsync {

    static void brewCoffee() {
        pause(600);
        System.out.println("  coffee brewed");
    }

    static void printLabel() {
        pause(400);
        System.out.println("  label printed");
    }

    static void main() throws InterruptedException {
        System.out.println("=== sequential ===");
        long t0 = System.currentTimeMillis();
        brewCoffee();
        printLabel();
        System.out.println("total: " + (System.currentTimeMillis() - t0) + " ms");

        System.out.println("=== parallel ===");
        long t1 = System.currentTimeMillis();
        Thread a = new Thread(SyncVsAsync::brewCoffee);
        Thread b = new Thread(SyncVsAsync::printLabel);
        a.start();
        b.start();
        a.join();               // czekamy, aż oba wątki skończą
        b.join();
        System.out.println("total: " + (System.currentTimeMillis() - t1) + " ms");
    }

    /** Usypia wątek i nie zaśmieca przykładów blokiem try-catch. */
    static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
