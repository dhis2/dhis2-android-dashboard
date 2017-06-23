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
        periodsList = new boolean[]{thisYear, quartersLastYear, last52Weeks, thisWeek, lastMonth,
                last14Days, monthsThisYear, last2SixMonths, yesterday, thisQuarter, last12Months,
                last5FinancialYears, thisSixMonth, lastQuarter, thisFinancialYear, last4Weeks,
                last3Months, thisDay, thisMonth, last5Years, last6BiMonths, lastFinancialYear,
                last6Months, last3Days, quartersThisYear, monthsLastYear, lastWeek, last7Days,
                thisBimonth, lastBimonth, lastSixMonth, lastYear, last12Weeks, last4Quarters};
        for (int i = 0; i < periodsList.length; i++) {
            if (periodsList[i]) return periodsStrings[i];
        }
        return "";
    }

    public boolean isThisYear() {
        return thisYear;
    }

    public void setThisYear(boolean thisYear) {
        this.thisYear = thisYear;
    }

    public boolean isQuartersLastYear() {
        return quartersLastYear;
    }

    public void setQuartersLastYear(boolean quartersLastYear) {
        this.quartersLastYear = quartersLastYear;
    }

    public boolean isLast52Weeks() {
        return last52Weeks;
    }

    public void setLast52Weeks(boolean last52Weeks) {
        this.last52Weeks = last52Weeks;
    }

    public boolean isThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(boolean thisWeek) {
        this.thisWeek = thisWeek;
    }

    public boolean isLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(boolean lastMonth) {
        this.lastMonth = lastMonth;
    }

    public boolean isLast14Days() {
        return last14Days;
    }

    public void setLast14Days(boolean last14Days) {
        this.last14Days = last14Days;
    }

    public boolean isMonthsThisYear() {
        return monthsThisYear;
    }

    public void setMonthsThisYear(boolean monthsThisYear) {
        this.monthsThisYear = monthsThisYear;
    }

    public boolean isLast2SixMonths() {
        return last2SixMonths;
    }

    public void setLast2SixMonths(boolean last2SixMonths) {
        this.last2SixMonths = last2SixMonths;
    }

    public boolean isYesterday() {
        return yesterday;
    }

    public void setYesterday(boolean yesterday) {
        this.yesterday = yesterday;
    }

    public boolean isThisQuarter() {
        return thisQuarter;
    }

    public void setThisQuarter(boolean thisQuarter) {
        this.thisQuarter = thisQuarter;
    }

    public boolean isLast12Months() {
        return last12Months;
    }

    public void setLast12Months(boolean last12Months) {
        this.last12Months = last12Months;
    }

    public boolean isLast5FinancialYears() {
        return last5FinancialYears;
    }

    public void setLast5FinancialYears(boolean last5FinancialYears) {
        this.last5FinancialYears = last5FinancialYears;
    }

    public boolean isThisSixMonth() {
        return thisSixMonth;
    }

    public void setThisSixMonth(boolean thisSixMonth) {
        this.thisSixMonth = thisSixMonth;
    }

    public boolean isLastQuarter() {
        return lastQuarter;
    }

    public void setLastQuarter(boolean lastQuarter) {
        this.lastQuarter = lastQuarter;
    }

    public boolean isThisFinancialYear() {
        return thisFinancialYear;
    }

    public void setThisFinancialYear(boolean thisFinancialYear) {
        this.thisFinancialYear = thisFinancialYear;
    }

    public boolean isLast4Weeks() {
        return last4Weeks;
    }

    public void setLast4Weeks(boolean last4Weeks) {
        this.last4Weeks = last4Weeks;
    }

    public boolean isLast3Months() {
        return last3Months;
    }

    public void setLast3Months(boolean last3Months) {
        this.last3Months = last3Months;
    }

    public boolean isThisDay() {
        return thisDay;
    }

    public void setThisDay(boolean thisDay) {
        this.thisDay = thisDay;
    }

    public boolean isThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(boolean thisMonth) {
        this.thisMonth = thisMonth;
    }

    public boolean isLast5Years() {
        return last5Years;
    }

    public void setLast5Years(boolean last5Years) {
        this.last5Years = last5Years;
    }

    public boolean isLast6BiMonths() {
        return last6BiMonths;
    }

    public void setLast6BiMonths(boolean last6BiMonths) {
        this.last6BiMonths = last6BiMonths;
    }

    public boolean isLastFinancialYear() {
        return lastFinancialYear;
    }

    public void setLastFinancialYear(boolean lastFinancialYear) {
        this.lastFinancialYear = lastFinancialYear;
    }

    public boolean isLast6Months() {
        return last6Months;
    }

    public void setLast6Months(boolean last6Months) {
        this.last6Months = last6Months;
    }

    public boolean isLast3Days() {
        return last3Days;
    }

    public void setLast3Days(boolean last3Days) {
        this.last3Days = last3Days;
    }

    public boolean isQuartersThisYear() {
        return quartersThisYear;
    }

    public void setQuartersThisYear(boolean quartersThisYear) {
        this.quartersThisYear = quartersThisYear;
    }

    public boolean isMonthsLastYear() {
        return monthsLastYear;
    }

    public void setMonthsLastYear(boolean monthsLastYear) {
        this.monthsLastYear = monthsLastYear;
    }

    public boolean isLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(boolean lastWeek) {
        this.lastWeek = lastWeek;
    }

    public boolean isLast7Days() {
        return last7Days;
    }

    public void setLast7Days(boolean last7Days) {
        this.last7Days = last7Days;
    }

    public boolean isThisBimonth() {
        return thisBimonth;
    }

    public void setThisBimonth(boolean thisBimonth) {
        this.thisBimonth = thisBimonth;
    }

    public boolean isLastBimonth() {
        return lastBimonth;
    }

    public void setLastBimonth(boolean lastBimonth) {
        this.lastBimonth = lastBimonth;
    }

    public boolean isLastSixMonth() {
        return lastSixMonth;
    }

    public void setLastSixMonth(boolean lastSixMonth) {
        this.lastSixMonth = lastSixMonth;
    }

    public boolean isLastYear() {
        return lastYear;
    }

    public void setLastYear(boolean lastYear) {
        this.lastYear = lastYear;
    }

    public boolean isLast12Weeks() {
        return last12Weeks;
    }

    public void setLast12Weeks(boolean last12Weeks) {
        this.last12Weeks = last12Weeks;
    }

    public boolean isLast4Quarters() {
        return last4Quarters;
    }

    public void setLast4Quarters(boolean last4Quarters) {
        this.last4Quarters = last4Quarters;
    }
}
