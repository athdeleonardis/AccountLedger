package Ledger;

import CSV.CSV;

import java.util.Arrays;

public class AccountLedgerToCSV {
    private AccountLedger accountLedger;

    public AccountLedgerToCSV setAccountLedger(AccountLedger accountLedger) {
        this.accountLedger = accountLedger;
        return this;
    }

    public CSV compile(String csvName) {
        CSV csv = new CSV(csvName);
        csv.setColumnNames(Arrays.asList("Account","Amount"));
        for (String accountName : accountLedger.getAccountNames()) {
            csv.addRow(Arrays.asList(accountName, ""+accountLedger.getAmount(accountName)));
        }
        return csv;
    }
}
