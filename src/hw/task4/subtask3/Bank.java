package hw.task4.subtask3;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    // Mapa bo musi być czytana po kluczu. Wystarczy HashMap bo struktura będzie tworzona w całości jeszcze przed uruchomieniem wątków.
    final Map<Long, BankAccountAtomic> accounts = new HashMap<>();

    public void openAccount(long id, long initialPln) {
        accounts.put(id, new BankAccountAtomic(initialPln));
    }

    /**
     * Suma sald wszystkich kont w groszach.
     */
    public long totalBalanceGr() {
        return accounts.values().stream()
                .mapToLong(BankAccountAtomic::balanceGr)
                .sum();
    }
}