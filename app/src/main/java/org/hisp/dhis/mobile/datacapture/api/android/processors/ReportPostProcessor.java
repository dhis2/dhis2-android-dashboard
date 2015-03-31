package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportPostEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportPostEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.ImportSummaries;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;


public class ReportPostProcessor extends AbsProcessor<ReportPostEvent, OnReportPostEvent> {
    private static String TAG = ReportPostProcessor.class.getSimpleName();

    public ReportPostProcessor(Context context, ReportPostEvent event) {
        super(context, event);
    }

    @Override
    public OnReportPostEvent process() {
        OnReportPostEvent event = new OnReportPostEvent();
        Report sReport = getEvent().getReport();

        final ReportHandler handler = new ReportHandler(getContext());
        final DbRow<Report> dbReport = handler.query(
                sReport.getOrgUnit(), sReport.getDataSet(), sReport.getPeriod(), true
        );
        handler.update(dbReport, ReportState.POSTING);

        final ResponseHolder<ImportSummaries> holder = new ResponseHolder<>();
        DHISManager.getInstance().postReport(new ApiRequestCallback<ImportSummaries>() {
            @Override
            public void onSuccess(Response response, ImportSummaries summaries) {
                holder.setResponse(response);
                holder.setItem(summaries);
                ImportSummaries.DataValueCount count = summaries.getDataValueCount();
                String output = "Imported: " + count.getImported() +
                        " Updated: " + count.getUpdated() +
                        " Deleted: " + count.getImported() +
                        " Ignored: " + count.getIgnored();
                Log.d(TAG, output);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
                e.printStackTrace();
            }
        }, dbReport.getItem());


        if (holder.getException() != null) {
            handler.update(dbReport, ReportState.PENDING);
        } else {
            handler.delete(dbReport);
        }

        event.setResponseHolder(holder);
        return event;
    }
}
