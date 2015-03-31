package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeekIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_FORMAT = "%s%s%s";
    private static final String DATE_LABEL_FORMAT = "%s%s %s - %s";
    private static final String W = "W";

    private boolean mAllowFP;
    private LocalDate mPeriod;
    private LocalDate mCheckDate;

    public WeekIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.withWeekOfWeekyear(1).withDayOfWeek(1));
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
            return currentDate.isAfter(date.plusWeeks(1));
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
        mPeriod = mPeriod.plusMonths(1);
        mPeriod = mPeriod.withWeekOfWeekyear(1).withDayOfWeek(1);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> previous() {
        mPeriod = mPeriod.minusYears(1);
        mPeriod = mPeriod.plusMonths(1);
        mPeriod = mPeriod.withWeekOfWeekyear(1).withDayOfWeek(1);
        return generatePeriod();
    }

    @Override
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);
        int counter = 0;
        int quantity = mCheckDate.weekOfWeekyear().getMaximumValue();

        while (hasNext(mCheckDate) && counter < quantity) {
            String year = mCheckDate.year().getAsString();
            String cWeekNumber = mCheckDate.weekOfWeekyear().getAsString();
            String cDate = mCheckDate.toString();
            String nDate = mCheckDate.plusWeeks(1).toString();

            String date = String.format(DATE_FORMAT, year, W, cWeekNumber);
            String label = String.format(DATE_LABEL_FORMAT, W, cWeekNumber, cDate, nDate);

            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            counter++;
            mCheckDate = mCheckDate.plusWeeks(1);
        }

        Collections.reverse(dates);
        return dates;
    }

}
