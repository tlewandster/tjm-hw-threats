package watki.ch07;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Blokuj jak najkrócej: ta sama praca z długą i z krótką sekcją krytyczną. */
public class ShortCriticalSection {

    record Report(String name) {}

    private static final Object LOCK = new Object();
    private static final List<Report> REPORTS = new ArrayList<>();

    /** Udaje kosztowną pracę: zapytanie do bazy, plik, HTTP. */
    static Report generateReport(String name) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new Report(name);
    }

    // ❌ źle: cała praca w sekcji krytycznej — wszyscy stoją w kolejce
    static void addReportSlow(String name) {
        synchronized (LOCK) {
            Report report = generateReport(name);
            REPORTS.add(report);
        }
    }

    // ✅ dobrze: policz poza zamkiem, zablokuj tylko na zapis do listy
    static void addReportFast(String name) {
        Report report = generateReport(name);
        synchronized (LOCK) {
            REPORTS.add(report);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        measure("work inside lock ", ShortCriticalSection::addReportSlow);
        measure("work outside lock", ShortCriticalSection::addReportFast);
    }

    static void measure(String label, Consumer<String> task) throws InterruptedException {
        synchronized (LOCK) {
            REPORTS.clear();
        }
        long start = System.nanoTime();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            String name = "report-" + i;
            threads[i] = new Thread(() -> task.accept(name));
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        long millis = (System.nanoTime() - start) / 1_000_000;
        System.out.println(label + ": " + REPORTS.size() + " reports in " + millis + " ms");
    }
}
