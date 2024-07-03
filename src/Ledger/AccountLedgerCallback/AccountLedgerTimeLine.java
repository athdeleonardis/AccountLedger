package Ledger.AccountLedgerCallback;

import CSV.CSV;
import Ledger.AccountLedger;

import java.util.Arrays;
import java.util.List;

public class AccountLedgerTimeLine implements AccountLedgerCallback {
    private AccountLedger accountLedger;
    private String accountOfInterest;
    private CSV timeline;
    private float total;
    private String currentDate;

    public AccountLedgerTimeLine(AccountLedger accountLedger, String accountOfInterest, String csvParentName, String startingDate, float startingTotal) {
        this.accountLedger = accountLedger;
        this.accountOfInterest = accountOfInterest;
        this.timeline = new CSV(csvParentName + "-" + "TimeLine");
        this.timeline.setColumnNames(Arrays.asList("Date", "Amount"));
        this.currentDate = startingDate;
        this.total = startingTotal;
    }

    public CSV getTimeline() {
        if (currentDate != null) {
            timeline.addRow(Arrays.asList(currentDate, ""+total));
            currentDate = null;
        }
        return this.timeline;
    }

    // returns if the date was updated
    private boolean updateDate(String newDate) {
        if (newDate.equals(currentDate))
            return false;
        if (currentDate != null) {
            timeline.addRow(Arrays.asList(currentDate, ""+total));
        }
        currentDate = newDate;
        return true;
    }

    private void update(String date) {
        updateDate(date);
        this.total = accountLedger.getAmount(accountOfInterest);
    }

    @Override
    public void update(String date, String type, String fromAccount, String toAccount, float amount) {
        update(date);
    }
}

