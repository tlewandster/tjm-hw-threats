package watki.ch13;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** invokeAll = chcę wszystkie wyniki. invokeAny = chcę najszybszy. */
public class InvokeAllAny {

    static Callable<String> replica(String name, long millis) {
        return () -> {
            Thread.sleep(millis);
            return name;
        };
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {
            List<Callable<String>> replicas = List.of(
                    replica("replica-eu", 500),
                    replica("replica-us", 200),
                    replica("replica-asia", 800));

            long t0 = System.currentTimeMillis();
            List<Future<String>> all = pool.invokeAll(replicas);
            System.out.println("invokeAll took "
                    + (System.currentTimeMillis() - t0) + " ms");
            for (Future<String> f : all) {
                System.out.println("  " + f.get());      // już gotowe, nie blokuje
            }

            long t1 = System.currentTimeMillis();
            String fastest = pool.invokeAny(replicas);
            System.out.println("invokeAny took "
                    + (System.currentTimeMillis() - t1) + " ms");
            System.out.println("  winner: " + fastest);
        }
    }
}
