package Ledger;

import CSV.CSV;

import java.util.List;
import java.util.Set;

public class LedgerAnalyzer {
    private CSV csvLedger;
    private CSVLedgerParser csvLedgerParser;
    private AccountGrouper accountGrouper;
    private AccountLedger accountLedger;

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
        Set<String> toAccounts = accountGrouper.getGroups(csvLedgerParser.getToAccount());
        float amount = csvLedgerParser.getAmount();
        for (String account : toAccounts) {
            accountLedger.add(account, amount);
        }
    }

    private void subtract() {
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        float amount = csvLedgerParser.getAmount();
        for (String account : fromAccounts) {
            accountLedger.subtract(account, amount);
        }
    }

    private void transfer() {
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        Set<String> toAccounts = accountGrouper.getGroups(csvLedgerParser.getToAccount());
        AccountGrouper.removeCommonAccounts(fromAccounts, toAccounts);
        float amount = csvLedgerParser.getAmount();
        transfer(fromAccounts, toAccounts, amount);
    }

    private void topUp() {
        Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
        String toAccount = csvLedgerParser.getToAccount();
        Set<String> toAccounts = accountGrouper.getGroups(toAccount);
        AccountGrouper.removeCommonAccounts(fromAccounts, toAccounts);
        float amount = accountLedger.getTopUpDifference(toAccount, csvLedgerParser.getAmount());
        transfer(fromAccounts, toAccounts, amount);
    }

    private void distribute() {
        List<String> toAccountsUngrouped = csvLedgerParser.getToAccounts();
        List<Float> percentages = csvLedgerParser.getPercentages();
        String fromAccount = csvLedgerParser.getFromAccount();
        float fromAccountAmount = accountLedger.getAmount(fromAccount);
        for (int i = 0; i < toAccountsUngrouped.size(); i++) {
            Set<String> fromAccounts = accountGrouper.getGroups(csvLedgerParser.getFromAccount());
            Set<String> toAccounts = accountGrouper.getGroups(toAccountsUngrouped.get(i));
            float amount = fromAccountAmount * percentages.get(i) / 100f;
            transfer(fromAccounts, toAccounts, amount);
        }
    }

    private void transfer(Set<String> fromAccounts, Set<String> toAccounts, float amount) {
        for (String account : fromAccounts) {
            accountLedger.subtract(account, amount);
        }
        for (String account : toAccounts) {
            accountLedger.add(account, amount);
        }
    }
}
