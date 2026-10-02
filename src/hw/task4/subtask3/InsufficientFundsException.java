package hw.task4.subtask3;

/**
 * Nieoznaczony (unchecked) – nie zmusza do try/catch w każdej lambdzie.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId, long balanceGr, long requestedGr) {
        super(String.format(
                "Attempt to make a withdraw of %d PLN from an account with ID %d and a balance of %d PLN.",
                requestedGr / 100,
                accountId,
                balanceGr / 100));
    }
}