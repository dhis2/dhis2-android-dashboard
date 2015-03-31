package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_FORMAT = "YYYYMMdd";
    private static final String DATE_LABEL_FORMAT = "%s %s, %s";

    private boolean mAllowFP;
    private LocalDate mPeriod;
    private LocalDate mCheckDate;

    public DayIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
    }

    @Override
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    private boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(date);
        }
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
        int quantity = mCheckDate.dayOfYear().getMaximumValue();

        while (hasNext(mCheckDate) && counter < quantity) {
            counter++;

            String date = mCheckDate.toString(DATE_FORMAT);

            String dName = mCheckDate.dayOfMonth().getAsString();
            String mName = mCheckDate.monthOfYear().getAsText();
            String yName = mCheckDate.year().getAsString();

            String label = String.format(DATE_LABEL_FORMAT, dName, mName, yName);

            mCheckDate = mCheckDate.plusDays(1);
            DateHolder holder = new DateHolder(date, label);
            dates.add(holder);
        }

        Collections.reverse(dates);
        return dates;
    }

}
