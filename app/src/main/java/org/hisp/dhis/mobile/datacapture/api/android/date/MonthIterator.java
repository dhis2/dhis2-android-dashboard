package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonthIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_FORMAT = "YYYYMM";
    private static final String DATE_LABEL_FORMAT = "%s %s";

    private boolean mAllowFP;
    private LocalDate mPeriod;
    private LocalDate mCheckDate;


    public MonthIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
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
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    private boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(date.plusMonths(1));
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
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);
        int counter = 0;

        while (hasNext(mCheckDate) && counter < 12) {
            String month = mCheckDate.monthOfYear().getAsShortText();
            String year = mCheckDate.year().getAsString();

            String date = mCheckDate.toString(DATE_FORMAT);
            String label = String.format(DATE_LABEL_FORMAT, month, year);

            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            counter++;
            mCheckDate = mCheckDate.plusMonths(1);
        }

        Collections.reverse(dates);
        return dates;
    }

}
