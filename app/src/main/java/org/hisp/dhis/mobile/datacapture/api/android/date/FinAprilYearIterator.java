package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinAprilYearIterator extends YearIterator {
    private static final String APRIL = "April";

    public FinAprilYearIterator(boolean allowFuturePeriod) {
        super(allowFuturePeriod);
    }

    @Override
    protected boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            LocalDate march = new LocalDate(date.getYear(), MAR, 31);
            return currentDate.isAfter(march);
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
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        int counter = 0;
        mCheckDate = new LocalDate(mPeriod);
        while (hasNext(mCheckDate) && counter < 10) {
            String dateStr = mCheckDate.minusYears(1).year().getAsString();
            String label = String.format(FIN_DATE_LABEL_FORMAT, APR_STR, dateStr,
                    MAR_STR, mCheckDate.year().getAsString());
            String date = dateStr + APRIL;
            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            mCheckDate = mCheckDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
