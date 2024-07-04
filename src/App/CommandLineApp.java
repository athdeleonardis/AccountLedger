package App;

import CSV.CSV;
import CSV.CSVFileReader;
import CSV.CSVFileWriter;
import Ledger.AccountGrouper;
import Ledger.AccountLedger;
import Ledger.AccountLedgerCallback.AccountLedgerCallbackHandler;
import Ledger.AccountLedgerCallback.AccountLedgerSplit;
import Ledger.AccountLedgerCallback.AccountLedgerTimeLine;
import Ledger.AccountLedgerToCSV;
import Ledger.LedgerAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CommandLineApp {
    private static final String argDebugLog = "-d";
    private static final String argInputLedger = "-i"; // -i ledgerName
    private static final String argInputSummaries = "-s"; // -s acc1,acc2,acc3... Accounts to make summary files of
    private static final String argGroupAccounts = "-g"; // -g group acc1,acc2,acc3...
    private static final String argSplitAccount = "-sp"; // -sp accountName

    public static void main(String[] args) {
        boolean doDebugLog = false;
        CSV csvLedger = null;
        AccountLedger accountLedger = new AccountLedger();
        AccountGrouper accountGrouper = new AccountGrouper();
        AccountLedgerCallbackHandler callbackHandler = new AccountLedgerCallbackHandler();

        LedgerAnalyzer ledgerAnalyzer = new LedgerAnalyzer()
                .setAccountGrouper(accountGrouper)
                .setCallbackHandler(callbackHandler)
                .setAccountLedger(accountLedger);

        Set<String> accountsToSummarize = new TreeSet<>();
        List<AccountLedgerTimeLine> timeLines = new ArrayList<>();
        List<String> accountsToSplit = new ArrayList<>();
        List<AccountLedgerSplit> splits = new ArrayList<>();

        // Read args
        int argi = 0;
        while (argi < args.length) {
            switch (args[argi]) {
                case argDebugLog:
                    System.out.println("Enabling debug logging");
                    doDebugLog = true;
                    argi++;
                    break;
                case argInputLedger:
                    if (csvLedger != null) {
                        System.out.println("Error: Ledger file already provided.");
                        System.exit(1);
                    }
                    argi++;
                    csvLedger = new CSV(args[argi]);
                {
                    CSVFileReader csvFileReader = new CSVFileReader()
                            .setCSV(csvLedger)
                            .read("data/" + args[argi] + ".csv");
                    if (!csvFileReader.success()) {
                        System.out.println("Error: failed to read file provided.");
                        System.exit(1);
                    }
                    ledgerAnalyzer.setCSVLedger(csvLedger);
                }
                if (doDebugLog) {
                    System.out.println("Contents of CSV '" + csvLedger.getName() + "':");
                    System.out.println(csvLedger);
                }
                argi++;
                break;
                case argInputSummaries:
                    argi++;
                    String[] accounts1 = args[argi].split(",");
                    for (String acc1 : accounts1)
                        accountsToSummarize.add(acc1);
                    argi++;
                    break;
                case argGroupAccounts:
                    argi++;
                    String group = args[argi];
                    argi++;
                    String[] accounts2 = args[argi].split(",");
                    for (String acc2 : accounts2)
                        accountGrouper.setRelationship(group, acc2);
                    argi++;
                    break;
                case argSplitAccount:
                    argi++;
                    accountsToSplit.add(args[argi]);
                    argi++;
                    break;
                default:
                    System.out.println("Error: Unexpected argument '" + args[argi] + "'.");
                    System.exit(1);
                    break;
            }
        }

        if (csvLedger == null) {
            System.out.println("Error: no file provided.");
            System.exit(1);
        }

        for (String accToSummarize : accountsToSummarize) {
            AccountLedgerTimeLine accountLedgerTimeLine = new AccountLedgerTimeLine(
                    accountLedger,
                    accToSummarize,
                    csvLedger.getName(),
                    null,
                    0
            );
            callbackHandler.addCallback(accToSummarize, accountLedgerTimeLine);
            timeLines.add(accountLedgerTimeLine);
        }

        for (String accToSplit : accountsToSplit) {
            AccountLedgerSplit split = new AccountLedgerSplit(csvLedger.getName(), accToSplit);
            callbackHandler.addCallback(accToSplit, split);
            splits.add(split);
        }

        ledgerAnalyzer.analyze();

        // Write analysis to files

        CSVFileWriter csvFileWriter = new CSVFileWriter();

        {
            CSV accountLedgerSummary = new AccountLedgerToCSV()
                    .setAccountLedger(accountLedger)
                    .compile(csvLedger.getName() + "-Summary");
            if (doDebugLog) {
                System.out.println("Contents of CSV '" + accountLedgerSummary.getName() + "'.");
                System.out.println(accountLedgerSummary);
            }
            csvFileWriter
                    .setCSV(accountLedgerSummary)
                    .compile("data/" + accountLedgerSummary.getName() + ".csv");
        }

        for (AccountLedgerTimeLine timeLine : timeLines) {
            CSV csvTimeLine = timeLine.getTimeline();
            if (doDebugLog) {
                System.out.println("Contents of CSV '" + csvTimeLine.getName() + "':");
                System.out.println(csvTimeLine);
            }
            csvFileWriter
                    .setCSV(csvLedger)
                    .compile("data/" + csvTimeLine.getName() + ".csv");
        }

        for (AccountLedgerSplit split : splits) {
            CSV csvSplit = split.getCSV();
            if (doDebugLog) {
                System.out.println("Contents of CSV '" + csvSplit.getName() + "':");
                System.out.println(csvSplit);
            }
            csvFileWriter
                    .setCSV(csvSplit)
                    .compile("data/" + csvSplit.getName() + ".csv");
        }
    }
}
