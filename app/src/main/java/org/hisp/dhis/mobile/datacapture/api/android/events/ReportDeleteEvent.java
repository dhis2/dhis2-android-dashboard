package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.models.Report;

public class ReportDeleteEvent {
    private Report report;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
