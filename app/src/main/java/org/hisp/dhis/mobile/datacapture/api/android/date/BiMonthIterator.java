package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiMonthIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String B1 = "01B";
    private static final String B3 = "03B";
    private static final String B5 = "05B";
    private static final String B7 = "07B";
    private static final String B9 = "09B";
    private static final String B11 = "11B";

    private static final String DATE_LABEL_FORMAT = "%s - %s %s";

    private boolean mAllowFP;
    private LocalDate mCheckDate;
    private LocalDate mPeriod;

    public BiMonthIterator(boolean allowFuturePeriod) {
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
            return currentDate.isAfter(date.plusMonths(2));
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
        int counter = 0;
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);

        while (hasNext(mCheckDate) && counter < 6) {
            int cMonth = mCheckDate.getMonthOfYear();
            String year = mCheckDate.year().getAsString();

            String date;
            String label;

            if (cMonth < FEB) {
                date = year + B1;
                label = String.format(DATE_LABEL_FORMAT, JAN_STR, FEB_STR, year);
            } else if ((cMonth >= FEB) && (cMonth < APR)) {
                date = year + B3;
                label = String.format(DATE_LABEL_FORMAT, MAR_STR, APR_STR, year);
            } else if ((cMonth >= APR) && (cMonth < JUN)) {
                date = year + B5;
                label = String.format(DATE_LABEL_FORMAT, MAY_STR, JUN_STR, year);
            } else if ((cMonth >= JUN) && (cMonth < AUG)) {
                date = year + B7;
                label = String.format(DATE_LABEL_FORMAT, JUL_STR, AUG_STR, year);
            } else if ((cMonth >= AUG) && (cMonth < OCT)) {
                date = year + B9;
                label = String.format(DATE_LABEL_FORMAT, SEP_STR, OCT_STR, year);
            } else {
                date = year + B11;
                label = String.format(DATE_LABEL_FORMAT, NOV_STR, DEC_STR, year);
            }

            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            counter++;
            mCheckDate = mCheckDate.plusMonths(2);
        }

        Collections.reverse(dates);
        return dates;
    }

}
