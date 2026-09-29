package hw.task4;

import java.util.Objects;

public class BankAccount {

    private final Long id;
    private long balanceGr;                 // saldo w GROSZACH

    public BankAccount(Long id, long initialPln) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
        this.balanceGr = initialPln * 100;
    }

    public synchronized void deposit(long amountGr) {
        if (amountGr <= 0) {
            throw new IllegalArgumentException("Cannot deposit a non-positive amount");
        } else {
            this.balanceGr += amountGr;
        }
    }

    public synchronized void withdraw(long amountGr) {
        // TODO: odrzuć amountGr <= 0; jeśli brakuje środków -> InsufficientFundsException;
        //       w przeciwnym razie odejmij
        if (amountGr <= 0) {
            throw new IllegalArgumentException("Cannot withdraw a non-positive amount");
        } else if (amountGr > this.balanceGr) {
            throw new InsufficientFundsException(this.id, this.balanceGr, amountGr);
        } else {
            this.balanceGr -= amountGr;
        }
    }

    public synchronized long getBalanceGr() {
        return this.balanceGr;
    }

    public Long getId() {
        return this.id;                          // pole final – synchronizacja niepotrzebna
    }
}