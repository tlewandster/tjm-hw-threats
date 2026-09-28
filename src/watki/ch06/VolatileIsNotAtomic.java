package watki.ch06;

/** volatile daje WIDOCZNOŚĆ, ale nie robi z ++ operacji niepodzielnej. */
public class VolatileIsNotAtomic {

    static volatile int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(VolatileIsNotAtomic::countMany);
        Thread t2 = new Thread(VolatileIsNotAtomic::countMany);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("volatile counter: expected 2000000, got " + counter);
    }

    static void countMany() {
        for (int i = 0; i < 1_000_000; i++) {
            counter++;              // wciąż load -> add -> store
        }
    }
}
