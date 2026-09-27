package watki.ch04;

/** Wątek daemon nie przedłuża życia JVM — user thread tak. */
public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {
        Thread heartbeat = new Thread(() -> {
            int tick = 0;
            while (true) {
                System.out.println("  [daemon] tick " + (++tick));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "heartbeat");

        heartbeat.setDaemon(true);   // MUSI być przed start()
        heartbeat.start();

        Thread.sleep(700);
        System.out.println("main ends -> JVM kills the daemon thread");
    }
}
