package Ledger.AccountLedgerCallback;

import Ledger.AccountLedger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AccountLedgerCallbackHandler {
    private HashMap<String, List<AccountLedgerCallback>> accountToCallbacksMap;

    public AccountLedgerCallbackHandler() {
        this.accountToCallbacksMap = new HashMap<>();
    }

    public AccountLedgerCallbackHandler addCallback(String account, AccountLedgerCallback callback) {
        if (!accountToCallbacksMap.containsKey(account))
            accountToCallbacksMap.put(account, new ArrayList<>());
        accountToCallbacksMap.get(account).add(callback);
        return this;
    }

    public AccountLedgerCallbackHandler update(String account, String date, String type, String fromAccount, String toAccount, float amount) {
        if (!accountToCallbacksMap.containsKey(account))
            return this;
        for (AccountLedgerCallback callback : accountToCallbacksMap.get(account)) {
            callback.update(date, type, toAccount, fromAccount, amount);
        }
        return this;
    }

    public AccountLedgerCallbackHandler update(Collection<String> accounts, String date, String type, String toAccount, String fromAccount, float amount) {
        for (String account : accounts) {
            update(account, date, type, toAccount, fromAccount, amount);
        }
        return this;
    }
}
