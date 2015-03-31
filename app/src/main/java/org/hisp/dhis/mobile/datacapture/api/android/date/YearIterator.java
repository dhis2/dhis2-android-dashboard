package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YearIterator extends CustomDateIteratorClass<List<DateHolder>> {
    public static final int DECADE = 10;
    public static final String FIN_DATE_LABEL_FORMAT = "%s %s - %s %s";

    protected boolean mAllowFP;
    protected LocalDate mPeriod;
    protected LocalDate mCheckDate;

    public YearIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
    }

    @Override
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    protected boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(date.plusYears(1));
        }
    }

    @Override
    public List<DateHolder> next() {
        mPeriod = mPeriod.plusYears(DECADE);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> previous() {
        mPeriod = mPeriod.minusYears(DECADE);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> current() {
        return previous();
    }

    @Override
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        int counter = 0;
        mCheckDate = new LocalDate(mPeriod);

        while (hasNext(mCheckDate) && counter < 10) {
            String dateStr = mCheckDate.year().getAsString();
            DateHolder dateHolder = new DateHolder(dateStr, dateStr);
            dates.add(dateHolder);

            mCheckDate = mCheckDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
