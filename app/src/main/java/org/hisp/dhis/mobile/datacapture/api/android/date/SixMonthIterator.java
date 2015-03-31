package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SixMonthIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_LABEL_FORMAT = "%s - %s %s";
    private static final String S1 = "S1";
    private static final String S2 = "S2";

    private boolean mAllowFP;
    private LocalDate mPeriod;
    private LocalDate mCheckDate;

    public SixMonthIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
    }

    @Override
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    private boolean hasNext(LocalDate cDate) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(cDate.plusMonths(6));
        }
    }

    @Override
    public List<DateHolder> next() {
        mPeriod = mPeriod.plusYears(1);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> previous() {
        mPeriod = mPeriod.minusYears(1);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> current() {
        if (!hasNext()) {
            return previous();
        } else {
            return generatePeriod();
        }
    }

    @Override
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);
        int counter = 0;

        while (hasNext(mCheckDate) && counter < 2) {
            String year = mCheckDate.year().getAsString();
            String label;
            String date;

            if (mCheckDate.getMonthOfYear() > JUN) {
                label = String.format(DATE_LABEL_FORMAT, JUL_STR, DEC_STR, year);
                date = year + S2;
            } else {
                label = String.format(DATE_LABEL_FORMAT, JAN_STR, JUN_STR, year);
                date = year + S1;
            }

            mCheckDate = mCheckDate.plusMonths(6);
            counter++;

            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);
        }

        Collections.reverse(dates);
        return dates;
    }
}