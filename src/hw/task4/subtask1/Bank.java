package hw.task4.subtask1;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    // Mapa bo musi być czytana po kluczu. Wystarczy HashMap bo struktura będzie tworzona w całości jeszcze przed uruchomieniem wątków.
    private final Map<Long, BankAccount> accounts = new HashMap<>();

    public void openAccount(long id, long initialPln) {
        accounts.put(id, new BankAccount(id, initialPln));
    }

    /**
     * Atomowy przelew: albo obie strony, albo żadna. NIE może się zakleszczyć.
     */
    public void transfer(long fromId, long toId, long amountGr) {
        BankAccount fromAccount = accounts.get(fromId);
        BankAccount toAccount = accounts.get(toId);
        BankAccount first = fromAccount.getId() < toAccount.getId() ? fromAccount : toAccount;
        BankAccount second = fromAccount.getId() < toAccount.getId() ? toAccount : fromAccount;

        synchronized (first) {
            synchronized (second) {
                fromAccount.withdraw(amountGr);
                toAccount.deposit(amountGr);
            }
        }
    }

    /**
     * Suma sald wszystkich kont w groszach.
     */
    public long totalBalanceGr() {
        return accounts.values().stream()
                .mapToLong(BankAccount::getBalanceGr)
                .sum();
    }
}