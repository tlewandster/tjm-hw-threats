package hw.task4.subtask2;


import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccountLock {

    private final Long id;
    private final ReentrantLock lock = new ReentrantLock();
    private long balanceGr;                 // saldo w GROSZACH

    public BankAccountLock(Long id, long initialPln) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
        this.balanceGr = initialPln * 100;
    }

    public void depositUnlocked(long amountGr) {
        if (amountGr <= 0) {
            throw new IllegalArgumentException("Cannot deposit a non-positive amount");
        } else {
            this.balanceGr += amountGr;
        }
    }

    public void withdrawUnlocked(long amountGr) {
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

    ReentrantLock lock() {
        return lock;
    }
}