package org.hisp.dhis.mobile.datacapture.api.android.date;

import org.joda.time.LocalDate;

public abstract class CustomDateIteratorClass<T> implements CustomDateIterator<T> {
    protected static final int JAN = 1;
    protected static final int FEB = 2;
    protected static final int MAR = 3;
    protected static final int APR = 4;
    protected static final int MAY = 5;
    protected static final int JUN = 6;
    protected static final int JUL = 7;
    protected static final int AUG = 8;
    protected static final int SEP = 9;
    protected static final int OCT = 10;
    protected static final int NOV = 11;
    protected static final int DEC = 12;

    protected static final String JAN_STR;
    protected static final String FEB_STR;
    protected static final String MAR_STR;
    protected static final String APR_STR;
    protected static final String MAY_STR;
    protected static final String JUN_STR;
    protected static final String JUL_STR;
    protected static final String AUG_STR;
    protected static final String SEP_STR;
    protected static final String OCT_STR;
    protected static final String NOV_STR;
    protected static final String DEC_STR;

    static {
        LocalDate lDate = new LocalDate();

        JAN_STR = lDate.withMonthOfYear(JAN).monthOfYear().getAsShortText();
        FEB_STR = lDate.withMonthOfYear(FEB).monthOfYear().getAsShortText();
        MAR_STR = lDate.withMonthOfYear(MAR).monthOfYear().getAsShortText();
        APR_STR = lDate.withMonthOfYear(APR).monthOfYear().getAsShortText();
        MAY_STR = lDate.withMonthOfYear(MAY).monthOfYear().getAsShortText();
        JUN_STR = lDate.withMonthOfYear(JUN).monthOfYear().getAsShortText();
        JUL_STR = lDate.withMonthOfYear(JUL).monthOfYear().getAsShortText();
        AUG_STR = lDate.withMonthOfYear(AUG).monthOfYear().getAsShortText();
        SEP_STR = lDate.withMonthOfYear(SEP).monthOfYear().getAsShortText();
        OCT_STR = lDate.withMonthOfYear(OCT).monthOfYear().getAsShortText();
        NOV_STR = lDate.withMonthOfYear(NOV).monthOfYear().getAsShortText();
        DEC_STR = lDate.withMonthOfYear(DEC).monthOfYear().getAsShortText();
    }

    protected LocalDate currentDate;

    public CustomDateIteratorClass() {
        currentDate = new LocalDate();
    }

    public boolean hasPrevious() {
        return true;
    }

    public abstract T current();

    public abstract boolean hasNext();

    public abstract T next();

    public abstract T previous();

    protected abstract T generatePeriod();
}
