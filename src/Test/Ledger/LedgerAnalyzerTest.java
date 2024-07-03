package Test.Ledger;

import CSV.CSV;
import CSV.CSVFileWriter;
import CSV.CSVFileReader;
import Ledger.*;
import Ledger.AccountLedgerCallback.AccountLedgerCallbackHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LedgerAnalyzerTest {
    public static void main(String[] args) {
        CSV csvLedger = new CSV("CSVLedger");
        {
            CSVFileReader csvFileReader = new CSVFileReader();
            csvFileReader.setCSV(csvLedger);
            csvFileReader.read("sample/input/test_ledger.csv");
        }
        System.out.println("Contents of CSV '" + csvLedger.getName() + "':");
        System.out.println(csvLedger);

        // Alice and Bob are added to the group "Relationship"
        AccountGrouper accountGrouper = new AccountGrouper();
        accountGrouper.setRelationship("Relationship", "Alice");
        accountGrouper.setRelationship("Relationship", "Bob");

        AccountLedger accountLedger = new AccountLedger();

        AccountLedgerCallbackHandler callbackHandler = new AccountLedgerCallbackHandler();

        new LedgerAnalyzer()
                .setAccountLedger(accountLedger)
                .setCSVLedger(csvLedger)
                .setAccountGrouper(accountGrouper)
                .setCallbackHandler(callbackHandler)
                .analyze();

        AccountLedgerToCSV accountLedgerToCSV = new AccountLedgerToCSV();
        accountLedgerToCSV.setAccountLedger(accountLedger);
        CSV ledgerSummary = accountLedgerToCSV.compile("LedgerSummary");
        System.out.println("Contents of CSV '" + ledgerSummary.getName() + "':");
        System.out.println(ledgerSummary);

        CSVFileWriter csvFileWriter = new CSVFileWriter();
        csvFileWriter.setCSV(csvLedger);
        csvFileWriter.compile("sample/output/test_ledger_summary.csv");
    }
}

