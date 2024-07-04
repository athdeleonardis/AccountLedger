package Ledger;

import CSV.CSV;
import Ledger.AccountLedgerCallback.AccountLedgerCallbackHandler;

import java.util.List;
import java.util.Set;

public class LedgerAnalyzer {
    private CSV csvLedger;
    private CSVLedgerParser csvLedgerParser;
    private AccountGrouper accountGrouper;
    private AccountLedger accountLedger;
    private AccountLedgerCallbackHandler callbackHandler;

    public LedgerAnalyzer() {
        this.csvLedgerParser = new CSVLedgerParser();
    }

    public LedgerAnalyzer setCSVLedger(CSV csvLedger) {
        this.csvLedger = csvLedger;
        csvLedgerParser.setCSVLedger(csvLedger);
        return this;
    }

    public LedgerAnalyzer setAccountGrouper(AccountGrouper accountGrouper) {
        this.accountGrouper = accountGrouper;
        return this;
    }

    public LedgerAnalyzer setAccountLedger(AccountLedger accountLedger) {
        this.accountLedger = accountLedger;
        return this;
    }

    public LedgerAnalyzer setCallbackHandler(AccountLedgerCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
        return this;
    }

    public LedgerAnalyzer analyze() {
        while (csvLedgerParser.isAvailable()) {
            csvLedgerParser.readRow();
            String type = csvLedgerParser.getType();
            switch (type) {
                case "Add":
                    add();
                    break;
                case "Subtract":
                    subtract();
                    break;
                case "Transfer":
                    transfer();
                    break;
                case "TopUp":
                    topUp();
                    break;
                case "Distribute":
                    distribute();
                    break;
            }
        }

        return this;
    }

    private void add() {
        String date = csvLedgerParser.getDate();
        String fromAccount = csvLedgerParser.getFromAccount();
        Set<String> toAccounts = accountGrouper.getGroups(csvLedgerParser.getToAccount());
        float amount = csvLedgerParser.getAmount();
        for (String account : toAccounts) {
            accountLedger.add(account, amount);
            callbackHandler.update(
                    account,
                    date,
                    "Add",
                    fromAccount,
                    account,
                    amount
            );
        }
    }

    private void subtract() {
        String date = csvLedgerParser.getDate();
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        String toAccount = csvLedgerParser.getToAccount();
        float amount = csvLedgerParser.getAmount();
        for (String account : fromAccounts) {
            accountLedger.subtract(account, amount);
            callbackHandler.update(
                    account,
                    date,
                    "Subtract",
                    account,
                    toAccount,
                    amount
            );
        }
    }

    private void transfer() {
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        Set<String> toAccounts = accountGrouper.getGroups(csvLedgerParser.getToAccount());
        AccountGrouper.removeCommonAccounts(fromAccounts, toAccounts);
        float amount = csvLedgerParser.getAmount();
        transfer(null, fromAccounts, toAccounts, amount);
    }

    private void topUp() {
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        String toAccount = csvLedgerParser.getToAccount();
        Set<String> toAccounts = accountGrouper.getGroups(toAccount);
        AccountGrouper.removeCommonAccounts(fromAccounts, toAccounts);
        float amount = accountLedger.getTopUpDifference(toAccount, csvLedgerParser.getAmount());
        transfer("TopUp", fromAccounts, toAccounts, amount);
    }

    private void distribute() {
        List<String> toAccountsUngrouped = csvLedgerParser.getToAccounts();
        List<Float> percentages = csvLedgerParser.getPercentages();
        String fromAccount = csvLedgerParser.getFromAccount();
        float fromAccountAmount = accountLedger.getAmount(fromAccount);
        for (int i = 0; i < toAccountsUngrouped.size(); i++) {
            Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
            Set<String> toAccounts = accountGrouper.getGroups(toAccountsUngrouped.get(i));
            AccountGrouper.removeCommonAccounts(fromAccounts, toAccounts);
            float amount = fromAccountAmount * percentages.get(i) / 100f;
            transfer("Distribute", fromAccounts, toAccounts, amount);
        }
    }

    private void transfer(String type, Set<String> fromAccounts, Set<String> toAccounts, float amount) {
        type = (type == null) ? "Transfer" : "Transfer-" + type;
        for (String account : fromAccounts) {
            accountLedger.subtract(account, amount);
        }
        for (String account : toAccounts) {
            accountLedger.add(account, amount);
        }
        String date = csvLedgerParser.getDate();
        String fromAccount = csvLedgerParser.getFromAccount();
        String toAccount = csvLedgerParser.getToAccount();
        for (String account : fromAccounts) {
            callbackHandler.update(account, date, type, account, toAccount, amount);
        }
        for (String account : toAccounts) {
            callbackHandler.update(account, date, type, fromAccount, account, amount);
        }
    }
}
