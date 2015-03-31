package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinOctYearIterator extends YearIterator {
    private static final String OCTOBER = "Oct";

    public FinOctYearIterator(boolean allowFuturePeriod) {
        super(allowFuturePeriod);
    }

    @Override
    protected boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            LocalDate sep = new LocalDate(date.getYear(), SEP, 30);
            return currentDate.isAfter(sep);
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
            String label = String.format(FIN_DATE_LABEL_FORMAT, OCT_STR, dateStr,
                    SEP_STR, mCheckDate.year().getAsString());
            String date = dateStr + OCTOBER;
            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            mCheckDate = mCheckDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
