package watki.ch05;

/** counter++ to trzy operacje — i dlatego dwa wątki gubią inkrementacje. */
public class LostUpdates {

    static int counter = 0;

    static void countMany() {
        for (int i = 0; i < 1_000_000; i++) {
            counter++;              // load -> add -> store, każdy krok osobno
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int attempt = 1; attempt <= 5; attempt++) {
            counter = 0;
            Thread t1 = new Thread(LostUpdates::countMany);
            Thread t2 = new Thread(LostUpdates::countMany);
            Thread t3 = new Thread(LostUpdates::countMany);
            Thread t4 = new Thread(LostUpdates::countMany);
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            System.out.println("run " + attempt + ": expected 4000000, got " + counter);
        }
    }
}
