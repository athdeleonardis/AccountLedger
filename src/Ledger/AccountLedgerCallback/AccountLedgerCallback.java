package Ledger.AccountLedgerCallback;

public interface AccountLedgerCallback {
    void update(String date, String type, String fromAccount, String toAccount, float amount);
}
