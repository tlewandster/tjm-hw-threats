package hw.task4;

public class Bank {

    // TODO: struktura danych przechowująca konta, dostępna po id
    //       (wybór struktury jest częścią zadania – patrz "Co jest czym")

    public void openAccount(long id, long initialPln) {
        // TODO
    }

    /** Atomowy przelew: albo obie strony, albo żadna. NIE może się zakleszczyć. */
    public void transfer(long fromId, long toId, long amountGr) {
        // TODO
    }

    /** Suma sald wszystkich kont w groszach. */
    public long totalBalanceGr() {
        // TODO
        return 0;
    }
}