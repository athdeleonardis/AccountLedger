package Ledger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountLedger {
    private List<String> accountNames;
    private HashMap<String,Float> accountAmounts;

    public AccountLedger() {
        accountNames = new ArrayList<>();
        accountAmounts = new HashMap<>();
    }

    public boolean hasAccount(String accountName) {
        return accountAmounts.containsKey(accountName);
    }

    public AccountLedger addAccount(String accountName) {
        if (!accountAmounts.containsKey(accountName)) {
            accountAmounts.put(accountName, 0f);
            accountNames.add(accountName);
        }
        return this;
    }

    public List<String> getAccountNames() {
        return accountNames;
    }

    public float getAmount(String account) {
        if (!hasAccount(account))
            addAccount(account);
        return accountAmounts.get(account);
    }

    public AccountLedger add(String account, float amount) {
        if (!hasAccount(account))
            addAccount(account);
        accountAmounts.put(account, accountAmounts.get(account) + amount);
        return this;
    }

    public AccountLedger subtract(String account, float amount) {
        if (!hasAccount(account))
            addAccount(account);
        accountAmounts.put(account, accountAmounts.get(account) - amount);
        return this;
    }

    public AccountLedger transfer(String fromAccount, String toAccount, float amount) {
        subtract(fromAccount, amount);
        add(toAccount, amount);
        return this;
    }

    public AccountLedger topUp(String fromAccount, String toAccount, float amount) {
        float topUpAmount = getTopUpDifference(toAccount, amount);
        subtract(fromAccount, topUpAmount);
        add(toAccount, topUpAmount);
        return this;
    }

    public AccountLedger distrubute(String fromAccount, List<String> toAccounts, List<Float> percentages) {
        float fromAccountTotal = getAmount(fromAccount);
        for (int i = 0; i < toAccounts.size(); i++) {
            float amount = fromAccountTotal * percentages.get(i) / 100;
            transfer(fromAccount, toAccounts.get(i), amount);
        }
        return this;
    }

    public float getTopUpDifference(String account, float amount) {
        if (!hasAccount(account))
            return amount;
        return amount - accountAmounts.get(account);
    }
}
