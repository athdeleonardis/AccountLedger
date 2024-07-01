package Ledger;

import CSV.CSV;

import java.util.ArrayList;
import java.util.List;

public class CSVLedgerParser {
    private CSV csvLedger;
    private int row;

    private String date;
    private String type;
    private String fromAccount;
    private List<String> toAccounts;
    private List<Float> amounts;

    public CSVLedgerParser() {
        toAccounts = new ArrayList<>();
        amounts = new ArrayList<>();
    }

    public CSVLedgerParser setCSVLedger(CSV csvLedger) {
        this.csvLedger = csvLedger;
        return this;
    }

    public boolean isAvailable() {
        return row < csvLedger.numRows();
    }

    public CSVLedgerParser readRow() {
        date = csvLedger.getElement(row, "Date");
        type = csvLedger.getElement(row, "Type");
        fromAccount = csvLedger.getElement(row, "From");
        String toAccountStr = csvLedger.getElement(row, "To");
        toAccounts.clear();
        String amountStr = csvLedger.getElement(row, "Amount");
        amounts.clear();

        if (type.equals("Distribute")) {
            String[] accountsAndPercentages = toAccountStr.split(";");
            for (String accountPercentageStr : accountsAndPercentages) {
                String[] accountPercentagePair = accountPercentageStr.split(":");
                toAccounts.add(accountPercentagePair[0]);
                amounts.add(Float.parseFloat(accountPercentagePair[1]));
            }
        }
        else {
            toAccounts.add(toAccountStr);
            amounts.add(Float.parseFloat(amountStr));
        }

        row++;
        return this;
    }

    public String getDate() { return date; }
    public String getType() { return type; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccounts.get(0); }
    public List<String> getToAccounts() { return toAccounts; }
    public float getAmount() { return amounts.get(0); }
    public List<Float> getPercentages() { return amounts; }
}
