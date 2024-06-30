package Ledger.AccountLedgerCallback;

import Ledger.AccountLedger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountLedgerCallbackHandler {
    private HashMap<String, List<AccountLedgerCallback>> accountToCallbacksMap;
    private AccountLedger accountLedger;

    public AccountLedgerCallbackHandler() {
        this.accountToCallbacksMap = new HashMap<>();
    }

    public AccountLedgerCallbackHandler setAccountLedger(AccountLedger accountLedger) {
        this.accountLedger = accountLedger;
        return this;
    }

    public AccountLedgerCallbackHandler addCallback(String account, AccountLedgerCallback callback) {
        if (!accountToCallbacksMap.containsKey(account))
            accountToCallbacksMap.put(account, new ArrayList<>());
        accountToCallbacksMap.get(account).add(callback);
        return this;
    }

    public AccountLedgerCallbackHandler update(String account, String date) {
        if (!accountToCallbacksMap.containsKey(account))
            return this;
        float amount = accountLedger.getAmount(account);
        for (AccountLedgerCallback callback : accountToCallbacksMap.get(account)) {
            callback.update(date, amount);
        }
        return this;
    }
}
