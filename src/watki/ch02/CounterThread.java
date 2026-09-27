package watki.ch02;

public class CounterThread implements Runnable {
    static void main() throws InterruptedException {
        Thread thread1 = new Thread(new CounterThread(), "barista-1");
        Thread thread2 = new Thread(new CounterThread(), "barista-2");
        Thread thread3 = new Thread(new CounterThread(), "barista-3");
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
        }
    }
}




