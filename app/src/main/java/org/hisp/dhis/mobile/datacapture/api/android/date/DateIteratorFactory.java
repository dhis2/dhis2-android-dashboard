package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;

import java.util.List;

public class DateIteratorFactory {
    private static final String YEARLY = "Yearly";
    private static final String FINANCIAL_APRIL = "FinancialApril";
    private static final String FINANCIAL_JULY = "FinancialJuly";
    private static final String FINANCIAL_OCT = "FinancialOct";
    private static final String SIX_MONTHLY = "SixMonthly";
    private static final String QUARTERLY = "Quarterly";
    private static final String BIMONTHLY = "BiMonthly";
    private static final String MONTHLY = "Monthly";
    private static final String WEEKLY = "Weekly";
    private static final String DAILY = "Daily";
    private static final String WRONG_PERIOD_TYPE = "Wrong periodType";

    public static CustomDateIterator<List<DateHolder>> getDateIterator(String periodType, boolean allowFP) {

        if (periodType != null) {
            if (periodType.equals(YEARLY)) {
                return (new YearIterator(allowFP));
            } else if (periodType.equals(FINANCIAL_APRIL)) {
                return (new FinAprilYearIterator(allowFP));
            } else if (periodType.equals(FINANCIAL_JULY)) {
                return (new FinJulyYearIterator(allowFP));
            } else if (periodType.equals(FINANCIAL_OCT)) {
                return (new FinOctYearIterator(allowFP));
            } else if (periodType.equals(SIX_MONTHLY)) {
                return (new SixMonthIterator(allowFP));
            } else if (periodType.equals(QUARTERLY)) {
                return (new QuarterYearIterator(allowFP));
            } else if (periodType.equals(BIMONTHLY)) {
                return (new BiMonthIterator(allowFP));
            } else if (periodType.equals(MONTHLY)) {
                return (new MonthIterator(allowFP));
            } else if (periodType.equals(WEEKLY)) {
                return (new WeekIterator(allowFP));
            } else if (periodType.equals(DAILY)) {
                return (new DayIterator(allowFP));
            }
        }
        throw new IllegalArgumentException(WRONG_PERIOD_TYPE);
    }
}

