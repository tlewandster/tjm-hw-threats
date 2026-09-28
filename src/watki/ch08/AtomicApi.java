package watki.ch08;

import java.util.concurrent.atomic.AtomicInteger;

/** Najważniejsze metody AtomicInteger — wszystkie stoją na CAS. */
public class AtomicApi {

    public static void main(String[] args) {
        AtomicInteger stock = new AtomicInteger(10);

        System.out.println("get              = " + stock.get());
        System.out.println("incrementAndGet  = " + stock.incrementAndGet());
        System.out.println("getAndIncrement  = " + stock.getAndIncrement());
        System.out.println("after both       = " + stock.get());
        System.out.println("addAndGet(5)     = " + stock.addAndGet(5));

        // CAS: ustaw 100, ale tylko jeśli nadal jest 17
        System.out.println("compareAndSet    = " + stock.compareAndSet(17, 100));
        System.out.println("value now        = " + stock.get());

        // ta sama próba drugi raz już się nie uda — wartość się zmieniła
        System.out.println("second CAS       = " + stock.compareAndSet(17, 999));

        // getAndUpdate / updateAndGet: cała pętla CAS schowana w jednej metodzie
        System.out.println("getAndUpdate     = " + stock.getAndUpdate(v -> v * 2));
        System.out.println("updateAndGet     = " + stock.updateAndGet(v -> v + 10));
        System.out.println("accumulateAndGet = " + stock.accumulateAndGet(500, Math::max));
    }
}
