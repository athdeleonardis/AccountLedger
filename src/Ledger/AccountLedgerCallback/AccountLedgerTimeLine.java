package Ledger.AccountLedgerCallback;

import CSV.CSV;

import java.util.Arrays;
import java.util.List;

public class AccountLedgerTimeLine implements AccountLedgerCallback {
    private CSV timeline;
    private float total;
    private String currentDate;

    public AccountLedgerTimeLine(String parentName, String startingDate, float startingTotal) {
        this.timeline = new CSV(parentName + "-" + "TimeLine");
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

    @Override
    public void update(String date, float total) {
        updateDate(date);
        this.total = total;
    }
}

