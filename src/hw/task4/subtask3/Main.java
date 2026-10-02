package hw.task4.subtask3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static final int MIN_TRANSFER_PLN = 100;
    public static final int MAX_TRANSFER_PLN = 500;
    private static final int ACCOUNTS_NUMBER = 10;
    private static final long INITIAL_BALANCE_PLN = 1_000;
    private static final int TRANSFERS_NUMBER = 1_000;
    private static final int POOL_SIZE = 8;
    private static final int GR_IN_PLN = 100;
    private static final long EXPECTED = ACCOUNTS_NUMBER * INITIAL_BALANCE_PLN * GR_IN_PLN;

    static void main() {
        List<Transfer> transfers = createRandomTransfers();
        Bank bank = new Bank();
        for (int i = 0; i < ACCOUNTS_NUMBER; i++) {
            bank.openAccount(i + 1, INITIAL_BALANCE_PLN);
        }

        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

        CompletableFuture<?>[] all = new CompletableFuture[transfers.size()];
        AtomicInteger failedTransfers = new AtomicInteger();

        for (int i = 0; i < transfers.size(); i++) {
            Transfer transfer = transfers.get(i);
            all[i] = CompletableFuture.runAsync(() -> {
                BankAccountAtomic fromAccount = bank.accounts.get(transfer.fromIdAccount());
                BankAccountAtomic toAccount = bank.accounts.get(transfer.toIdAccount());
                if (fromAccount.tryWithdraw(transfer.amountTransferred())) {
                    toAccount.deposit(transfer.amountTransferred());
                } else {
                    failedTransfers.getAndIncrement();
                }
            }, pool);
        }
        CompletableFuture.allOf(all).join();
        pool.shutdown();
        if (bank.totalBalanceGr() != EXPECTED) {
            throw new AssertionError("NIEZMIENNIK ZŁAMANY – gdzieś jest race condition!");
        }
        System.out.printf("Sum of accounts %d, expected %d, failed transactions %d%n", bank.totalBalanceGr(), EXPECTED, failedTransfers.get());
    }

    private static List<Transfer> createRandomTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        Random rnd = new Random(20);
        for (int i = 0; i < TRANSFERS_NUMBER; i++) {
            long fromId, toId, amount;
            fromId = rnd.nextLong(ACCOUNTS_NUMBER) + 1;
            do {
                toId = rnd.nextLong(ACCOUNTS_NUMBER) + 1;
            } while (fromId == toId);
            amount = (rnd.nextLong(MAX_TRANSFER_PLN - MIN_TRANSFER_PLN + 1) + MIN_TRANSFER_PLN) * GR_IN_PLN;
//            System.out.printf("%d -> %d -> %d%n", fromId, amount, toId);
            transfers.add(new Transfer(fromId, toId, amount));
        }
        return transfers;
    }
}
