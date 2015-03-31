package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinJulyYearIterator extends YearIterator {
    private static final String JULY = "July";

    public FinJulyYearIterator(boolean allowFuturePeriod) {
        super(allowFuturePeriod);
    }

    @Override
    protected boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            LocalDate june = new LocalDate(date.getYear(), JUN, 30);
            return currentDate.isAfter(june);
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
            String label = String.format(FIN_DATE_LABEL_FORMAT, JUL_STR, dateStr,
                    JUN_STR, mCheckDate.year().getAsString());
            String date = dateStr + JULY;
            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            mCheckDate = mCheckDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
