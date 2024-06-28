package Test.Ledger;

import CSV.CSV;
import CSV.CSVFileReader;
import CSV.CSVFileWriter;
import Ledger.AccountLedger;
import Ledger.AccountLedgerToCSV;

import java.util.ArrayList;
import java.util.List;

public class AccountLedgerTest {
    public static void main(String[] args) {
        CSV csvLedger = new CSV("CSVLedger");
        CSVFileReader csvFileReader = new CSVFileReader();
        csvFileReader.setCSV(csvLedger);
        csvFileReader.read("sample/input/test_ledger.csv");
        System.out.println("Contents of CSV '" + csvLedger.getName() + "':");
        System.out.println(csvLedger.toString());

        AccountLedger accountLedger = new AccountLedger();
        for (int row = 0; row < csvLedger.numRows(); row++) {
            String type = csvLedger.getElement(row, "Type");
            String fromAccount = csvLedger.getElement(row, "From");
            String toAccount = csvLedger.getElement(row, "To");
            String amount = csvLedger.getElement(row, "Amount");

            switch (type) {
                case "Add":
                    accountLedger.add(toAccount, Float.parseFloat(amount));
                    break;
                case "Subtract":
                    accountLedger.subtract(fromAccount, Float.parseFloat(amount));
                    break;
                case "TopUp":
                    accountLedger.topUp(fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "Transfer":
                    accountLedger.transfer(fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "Distribute":
                    List<String> toAccounts = new ArrayList<>();
                    List<Float> percentages = new ArrayList<>();
                    String[] accountPercentages = toAccount.split(";");
                    for (String valPair : accountPercentages) {
                        String[] accountAndPercentage = valPair.split(":");
                        toAccounts.add(accountAndPercentage[0]);
                        percentages.add(Float.parseFloat(accountAndPercentage[1]));
                    }
                    accountLedger.distrubute(fromAccount, toAccounts, percentages);
                    break;
            }
        }


        AccountLedgerToCSV accountLedgerToCSV = new AccountLedgerToCSV();
        accountLedgerToCSV.setAccountLedger(accountLedger);
        CSV ledgerSummary = accountLedgerToCSV.compile("LedgerSummary");
        System.out.println("Contents of CSV '" + ledgerSummary.getName() + "':");
        System.out.println(ledgerSummary.toString());

        CSVFileWriter csvFileWriter = new CSVFileWriter();
        csvFileWriter.setCSV(csvLedger);
        csvFileWriter.compile("sample/output/test_ledger_summary.csv");
    }
}
