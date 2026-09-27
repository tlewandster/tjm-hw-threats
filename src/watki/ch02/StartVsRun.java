package watki.ch02;

/** Najczęstszy błąd początkującego: run() zamiast start(). */
public class StartVsRun {

    static void main() throws InterruptedException {
        Runnable task = () -> System.out.println("  executed by "
                + Thread.currentThread().getName());

        System.out.println("calling run():");
        new Thread(task).run();     // ← BŁĄD: zwykłe wywołanie metody

        System.out.println("calling start():");
        Thread t = new Thread(task);
        t.start();                  // ← poprawnie: nowy wątek systemowy
        t.join();
    }
}
