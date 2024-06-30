package Test.Ledger.AccountLedgerCallback;

import CSV.CSV;
import CSV.CSVFileReader;
import CSV.CSVFileWriter;
import Ledger.AccountLedger;
import Ledger.AccountLedgerCallback.AccountLedgerCallback;
import Ledger.AccountLedgerCallback.AccountLedgerCallbackHandler;
import Ledger.AccountLedgerCallback.AccountLedgerTimeLine;
import Ledger.AccountLedgerToCSV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AccountLedgerCallbackTest {
    public static void main(String[] args) {
        CSV csvLedger = new CSV("CSVLedger");
        CSVFileReader csvFileReader = new CSVFileReader();
        csvFileReader.setCSV(csvLedger);
        csvFileReader.read("sample/input/test_ledger.csv");
        System.out.println("Contents of CSV '" + csvLedger.getName() + "':");
        System.out.println(csvLedger.toString());

        AccountLedgerCallbackHandler accountLedgerCallbackHandler = new AccountLedgerCallbackHandler();
        AccountLedgerTimeLine aliceTimeLine = new AccountLedgerTimeLine("Test-Alice", null, 0);
        AccountLedgerTimeLine bobTimeLine = new AccountLedgerTimeLine("Test-Bob", null, 0);
        AccountLedgerTimeLine claireTimeLine = new AccountLedgerTimeLine("Test-Claire", null, 0);
        accountLedgerCallbackHandler.addCallback("Alice", aliceTimeLine);
        accountLedgerCallbackHandler.addCallback("Bob", bobTimeLine);
        accountLedgerCallbackHandler.addCallback("Claire", claireTimeLine);
        List<AccountLedgerTimeLine> timeLines = Arrays.asList(aliceTimeLine, bobTimeLine, claireTimeLine);


        AccountLedger accountLedger = new AccountLedger();
        accountLedgerCallbackHandler.setAccountLedger(accountLedger);
        for (int row = 0; row < csvLedger.numRows(); row++) {
            String date = csvLedger.getElement(row, "Date");
            String type = csvLedger.getElement(row, "Type");
            String fromAccount = csvLedger.getElement(row, "From");
            String toAccount = csvLedger.getElement(row, "To");
            String amount = csvLedger.getElement(row, "Amount");

            switch (type) {
                case "Add":
                    accountLedger.add(toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date);
                    break;
                case "Subtract":
                    accountLedger.subtract(fromAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(fromAccount, date);
                    break;
                case "TopUp":
                    accountLedger.topUp(fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date);
                    accountLedgerCallbackHandler.update(fromAccount, date);
                    break;
                case "Transfer":
                    accountLedger.transfer(fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date);
                    accountLedgerCallbackHandler.update(fromAccount, date);
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

                    accountLedgerCallbackHandler.update(fromAccount, date);
                    for (String toAcc : toAccounts)
                        accountLedgerCallbackHandler.update(toAcc, date);
                    break;
            }
        }

        AccountLedgerToCSV accountLedgerToCSV = new AccountLedgerToCSV();
        accountLedgerToCSV.setAccountLedger(accountLedger);
        CSV ledgerSummary = accountLedgerToCSV.compile("LedgerSummary");
        System.out.println("Contents of CSV '" + ledgerSummary.getName() + "':");
        System.out.println(ledgerSummary.toString());

        CSVFileWriter csvFileWriter = new CSVFileWriter();

        csvFileWriter.setCSV(csvLedger).compile("sample/output/test_ledger_summary.csv");

        for (AccountLedgerTimeLine timeLine : timeLines) {
            CSV csvTimeLine = timeLine.getTimeline();
            System.out.println("Contents of CSV '" + csvTimeLine.getName() + "':");
            System.out.println(csvTimeLine.toString());
            csvFileWriter.setCSV(csvTimeLine).compile("sample/output/" + csvTimeLine.getName() + ".csv");
        }
    }
}
