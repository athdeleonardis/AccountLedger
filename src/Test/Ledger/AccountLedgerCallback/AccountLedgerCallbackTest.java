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

        AccountLedger accountLedger = new AccountLedger();

        AccountLedgerCallbackHandler accountLedgerCallbackHandler = new AccountLedgerCallbackHandler();
        List<AccountLedgerTimeLine> timeLines = new ArrayList<>();
        {
            String[] accountsOfInterest = { "Alice", "Bob", "Claire" };
            for (String account : accountsOfInterest) {
                AccountLedgerTimeLine timeLine = new AccountLedgerTimeLine(
                        accountLedger,
                        account,
                        "Test-" + account,
                        null,
                        0
                );
                timeLines.add(timeLine);
                accountLedgerCallbackHandler.addCallback(account, timeLine);
            }
        }


        for (int row = 0; row < csvLedger.numRows(); row++) {
            String date = csvLedger.getElement(row, "Date");
            String type = csvLedger.getElement(row, "Type");
            String fromAccount = csvLedger.getElement(row, "From");
            String toAccount = csvLedger.getElement(row, "To");
            String amount = csvLedger.getElement(row, "Amount");

            switch (type) {
                case "Add":
                    accountLedger.add(toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "Subtract":
                    accountLedger.subtract(fromAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(fromAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "TopUp":
                    accountLedger.topUp(fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(fromAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "Transfer":
                    accountLedger.transfer(fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(toAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    accountLedgerCallbackHandler.update(fromAccount, date, type, fromAccount, toAccount, Float.parseFloat(amount));
                    break;
                case "Distribute":
                    List<String> toAccounts = new ArrayList<>();
                    List<Float> percentages = new ArrayList<>();
                    String[] accountPercentages = toAccount.split(";");
                    float fromAccAmount = accountLedger.getAmount(fromAccount);
                    for (String valPair : accountPercentages) {
                        String[] accountAndPercentage = valPair.split(":");
                        toAccounts.add(accountAndPercentage[0]);
                        percentages.add(Float.parseFloat(accountAndPercentage[1]));
                    }
                    accountLedger.distrubute(fromAccount, toAccounts, percentages);

                    float totalPercentage = 0;
                    for (int i = 0; i < toAccounts.size(); i++) {
                        totalPercentage += percentages.get(i);
                        String toAcc = toAccounts.get(i);
                        accountLedgerCallbackHandler.update(toAcc, date, "Transfer-Distribute", fromAccount, toAcc, fromAccAmount * percentages.get(i) / 100f);
                    }
                    accountLedgerCallbackHandler.update(fromAccount, date, "Transfer-Distribute", fromAccount, toAccount, fromAccAmount * totalPercentage / 100f);
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
