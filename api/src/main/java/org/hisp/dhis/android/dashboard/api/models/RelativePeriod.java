package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Table(databaseName = DbDhis.NAME)
public final class RelativePeriod extends BaseModel {

    @JsonIgnore
    private static final String[] periodsStrings =
            {"THIS_YEAR", "QUARTERS_LAST_YEAR", "LAST_52_WEEKS", "THIS_WEEK", "LAST_MONTH",
                    "LAST_14_DAYS", "MONTHS_THIS_YEAR", "LAST_2_SIXMONTHS", "YESTERDAY",
                    "THIS_QUARTER", "LAST_12_MONTHS", "LAST_5_FINANCIAL_YEARS", "THIS_SIX_MONTH",
                    "LAST_QUARTER", "THIS_FINANCIAL_YEAR", "LAST_4_WEEKS", "LAST_3_MONTHS",
                    "THIS_DAY", "THIS_MONTH", "LAST_5_YEARS", "LAST_6_BIMONTHS",
                    "LAST_FINANCIAL_YEAR", "LAST_6_MONTHS", "LAST_3_DAYS", "QUARTERS_THIS_YEAR",
                    "MONTHS_LAST_YEAR", "LAST_WEEK", "LAST_7_DAYS", "THIS_BIMONTH", "LAST_BIMONTH",
                    "LAST_SIX_MONTH", "LAST_YEAR", "LAST_12_WEEKS", "LAST_4_QUARTERS"};

    @JsonIgnore
    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    long id;

    boolean thisYear;
    boolean quartersLastYear;
    boolean last52Weeks;
    boolean thisWeek;
    boolean lastMonth;
    boolean last14Days;
    boolean monthsThisYear;
    boolean last2SixMonths;
    boolean yesterday;
    boolean thisQuarter;
    boolean last12Months;
    boolean last5FinancialYears;
    boolean thisSixMonth;
    boolean lastQuarter;
    boolean thisFinancialYear;
    boolean last4Weeks;
    boolean last3Months;
    boolean thisDay;
    boolean thisMonth;
    boolean last5Years;
    boolean last6BiMonths;
    boolean lastFinancialYear;
    boolean last6Months;
    boolean last3Days;
    boolean quartersThisYear;
    boolean monthsLastYear;
    boolean lastWeek;
    boolean last7Days;
    boolean thisBimonth;
    boolean lastBimonth;
    boolean lastSixMonth;
    boolean lastYear;
    boolean last12Weeks;
    boolean last4Quarters;

    @JsonIgnore
    private boolean[] periodsList;


    public RelativePeriod() {
        periodsList = new boolean[]{thisYear, quartersLastYear, last52Weeks, thisWeek, lastMonth,
                last14Days, monthsThisYear, last2SixMonths, yesterday, thisQuarter, last12Months,
                last5FinancialYears, thisSixMonth, lastQuarter, thisFinancialYear, last4Weeks,
                last3Months, thisDay, thisMonth, last5Years, last6BiMonths, lastFinancialYear,
                last6Months, last3Days, quartersThisYear, monthsLastYear, lastWeek, last7Days,
                thisBimonth, lastBimonth, lastSixMonth, lastYear, last12Weeks, last4Quarters};
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean[] getPeriodsList() {
        return periodsList;
    }

    public void setPeriodsList(boolean[] periodsList) {
        this.periodsList = periodsList;
    }

    public String getRelativePeriodString() {
        for (int i = 0; i < periodsList.length; i++) {
            if (periodsList[i]) return periodsStrings[i];
        }
        return "";
    }
}
