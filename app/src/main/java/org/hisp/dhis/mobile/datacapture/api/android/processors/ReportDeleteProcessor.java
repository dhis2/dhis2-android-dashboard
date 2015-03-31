package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;

import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportDeleteEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;

public class ReportDeleteProcessor extends AbsProcessor<ReportDeleteEvent, OnReportDeleteEvent> {

    public ReportDeleteProcessor(Context context, ReportDeleteEvent event) {
        super(context, event);
    }

    @Override
    public OnReportDeleteEvent process() {
        ReportHandler reportHandler = new ReportHandler(getContext());
        reportHandler.delete(getEvent().getReport());
        return new OnReportDeleteEvent();
    }
}
