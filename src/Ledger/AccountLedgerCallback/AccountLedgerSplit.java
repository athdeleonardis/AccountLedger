package Ledger.AccountLedgerCallback;

import CSV.CSV;

import java.util.Arrays;

public class AccountLedgerSplit implements AccountLedgerCallback {
    private CSV csvLedger;

    public AccountLedgerSplit(String csvParentName, String csvSubName) {
        csvLedger = new CSV(csvParentName + "-Split-" + csvSubName);
        csvLedger.setColumnNames(Arrays.asList("Date", "Type", "From", "To", "Amount"));
    }

    @Override
    public void update(String date, String type, String fromAccount, String toAccount, float amount) {
        csvLedger.addRow(Arrays.asList(date, type, fromAccount, toAccount, ""+amount));
    }

    public CSV getCSV() {
        return csvLedger;
    }
}
