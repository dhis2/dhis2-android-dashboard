package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.handlers.DataSetHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;
import org.hisp.dhis.mobile.datacapture.utils.DbUtils;

// TODO Fetch previously entered values if there are
public class ReportCreateProcessor extends AbsProcessor<ReportCreateEvent, OnReportCreateEvent> {
    private static final String TAG = ReportCreateProcessor.class.getSimpleName();

    public ReportCreateProcessor(Context context, ReportCreateEvent event) {
        super(context, event);
    }

    @Override
    public OnReportCreateEvent process() {
        ReportHandler reportHandler = new ReportHandler(getContext());
        DataSetHandler dataSetHandler = new DataSetHandler(getContext());
        OnReportCreateEvent event = new OnReportCreateEvent();

        Report eReport = getEvent().getReport();
        DbRow<Report> oldReport = reportHandler.query(
                eReport.getOrgUnit(), eReport.getDataSet(), eReport.getPeriod(), false
        );

        if (oldReport != null) {
            return event;
        }

        // first, we need to fetch values from server
        DataSet dataSet = null;
        try {
            dataSet = getReportFromServer();
        } catch (APIException e) {
            Log.e(TAG, e.getMessage());
        }

        // if if we don't have any values,
        // just fetch dataset from local DB
        if (dataSet == null) {
            dataSet = DbUtils.stripRow(dataSetHandler
                    .queryById(eReport.getDataSet(), true));
        }

        reportHandler.insert(eReport, dataSet);
        return event;
    }


    private DataSet getReportFromServer() throws APIException {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Report info = getEvent().getReport();
        final ResponseHolder<DataSet> holder = new ResponseHolder<>();

        DHISManager.getInstance().getReport(new ApiRequestCallback<DataSet>() {
            @Override
            public void onSuccess(Response response, DataSet dataSet) {
                Log.d(TAG, new String(response.getBody()));
                holder.setResponse(response);
                holder.setItem(dataSet);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, info.getOrgUnit(), info.getDataSet(), info.getPeriod());

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }
}
