package watki.ch04;

/** join() = poczekaj na wątek. interrupt() = poproś wątek, żeby skończył. */
public class JoinAndInterrupt {

    static void main() throws InterruptedException {
        Thread importer = new Thread(() -> {
            int rows = 0;
            // reagujemy na prośbę o przerwanie — to jest UMOWA, nie zabicie wątku
            while (!Thread.currentThread().isInterrupted()) {
                rows++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();   // przywracamy flagę
                }
            }
            System.out.println("  importer stopped after " + rows + " rows");
        }, "importer");

        importer.start();
        Thread.sleep(300);

        System.out.println("main: asking importer to stop");
        importer.interrupt();
        importer.join();                 // czekamy, aż faktycznie się zatrzyma
        System.out.println("main: importer is " + importer.getState());
    }
}
