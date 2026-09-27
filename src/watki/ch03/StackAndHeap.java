package watki.ch03;

import java.util.ArrayList;
import java.util.List;

/**
 * Zmienna lokalna = prywatny stos wątku. Obiekt = wspólna sterta.
 */
public class StackAndHeap {

    // pole statyczne żyje na stercie — WSZYSTKIE wątki widzą ten sam obiekt
//    static final List<String> SHARED_BASKET = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            List<String> sharedBasket = new ArrayList<>();
            int localCounter = 0;               // ← własna kopia w każdym wątku
            for (int i = 0; i < 3; i++) {
                localCounter++;
                sharedBasket.add(Thread.currentThread().getName() + "-" + i);
            }
            System.out.println(Thread.currentThread().getName()
                    + " localCounter = " + localCounter);
            System.out.println("shared basket size = " + sharedBasket.size());
        };

        Thread t1 = new Thread(task, "worker-A");
        Thread t2 = new Thread(task, "worker-B");
        System.out.println(t1.getState());
        t1.start();
        System.out.println(t1.getState());
        t2.start();
        t1.join();
        System.out.println(t1.getState());
        t2.join();

//        System.out.println("shared basket size = " + sharedBasket.size());
    }
}
