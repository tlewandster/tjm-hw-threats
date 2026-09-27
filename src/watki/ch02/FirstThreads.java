package watki.ch02;

/** Trzy sposoby uruchomienia wątku — i kto naprawdę wykonuje kod. */
public class FirstThreads {

    // sposób 1: własna klasa dziedzicząca po Thread
    static class LabelPrinter extends Thread {
        @Override
        public void run() {
            System.out.println("LabelPrinter runs on " + Thread.currentThread().getName());
        }
    }

    static void main() throws InterruptedException {
        System.out.println("main runs on " + Thread.currentThread().getName());

        Thread t1 = new LabelPrinter();
        t1.start();

        // sposób 2: Runnable jako lambda
        Runnable task = () -> System.out.println("task runs on "
                + Thread.currentThread().getName());
        Thread t2 = new Thread(task);
        t2.start();

        // sposób 3: wątek z własną nazwą — bezcenne w logach i w debuggerze
        Thread t3 = new Thread(task, "order-worker-1");
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        System.out.println("main is done");
    }
}
