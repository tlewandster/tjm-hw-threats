package hw.task4.subtask2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Bank {

    // Mapa bo musi być czytana po kluczu. Wystarczy HashMap bo struktura będzie tworzona w całości jeszcze przed uruchomieniem wątków.
    final Map<Long, BankAccountLock> accounts = new HashMap<>();

    public void openAccount(long id, long initialPln) {
        accounts.put(id, new BankAccountLock(id, initialPln));
    }

    /**
     * Atomowy przelew: albo obie strony, albo żadna. NIE może się zakleszczyć.
     */
    public boolean transfer(BankAccountLock fromAccount, BankAccountLock toAccount, long amountGr) throws InterruptedException {

        long deadline = System.currentTimeMillis() + 200;

        do {
            if (fromAccount.lock().tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    if (toAccount.lock().tryLock(50, TimeUnit.MILLISECONDS)) {
                        try {
                            fromAccount.withdrawUnlocked(amountGr);
                            toAccount.depositUnlocked(amountGr);
                            return true;
                        } finally {
                            toAccount.lock().unlock();
                        }
                    }
                } finally {
                    fromAccount.lock().unlock();
                    Thread.sleep(1);
                }
            }
        } while (System.currentTimeMillis() < deadline);
        return false;
    }

    /**
     * Suma sald wszystkich kont w groszach.
     */
    public long totalBalanceGr() {
        return accounts.values().stream()
                .mapToLong(BankAccountLock::getBalanceGr)
                .sum();
    }
}