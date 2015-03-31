package org.hisp.dhis.mobile.datacapture;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardItemDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.FieldValueChangeEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationUpdateTextEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportPostEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.UserAccountUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardCreateProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardItemDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DashboardUpdateProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.DatasetSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.FieldChangeValueProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.GetReportTableProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationSyncProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.InterpretationUpdateTextProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.LoginUserProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.ReportCreateProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.ReportDeleteProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.ReportPostProcessor;
import org.hisp.dhis.mobile.datacapture.api.android.processors.UserAccountUpdateProcessor;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class DHISService {
    private static final String TAG = DHISService.class.getSimpleName();
    private Context mContext;

    public DHISService(Context context) {
        mContext = isNull(context, "Context must not be null");
    }

    @Subscribe
    public void onUserLoginEvent(LoginUserEvent event) {
        executeTask(new LoginUserProcessor(mContext, event));
    }

    @Subscribe
    public void onDashboardSyncEvent(DashboardSyncEvent event) {
        executeTask(new DashboardSyncProcessor(mContext));
    }

    @Subscribe
    public void onGetReportTable(GetReportTableEvent event) {
        executeTask(new GetReportTableProcessor(event));
    }

    @Subscribe
    public void onDashboardDeleteEvent(DashboardDeleteEvent event) {
        executeTask(new DashboardDeleteProcessor(mContext, event));
    }

    @Subscribe
    public void onDashboardUpdateEvent(DashboardUpdateEvent event) {
        executeTask(new DashboardUpdateProcessor(mContext, event));
    }

    @Subscribe
    public void onDashboardItemDeleteEvent(DashboardItemDeleteEvent event) {
        executeTask(new DashboardItemDeleteProcessor(mContext, event));
    }

    @Subscribe
    public void onDashboardCreateEvent(DashboardCreateEvent event) {
        executeTask(new DashboardCreateProcessor(event));
    }

    @Subscribe
    public void onInterpretationsSyncEvent(InterpretationSyncEvent event) {
        executeTask(new InterpretationSyncProcessor(mContext));
    }

    @Subscribe
    public void onInterpretationDeleteEvent(InterpretationDeleteEvent event) {
        executeTask(new InterpretationDeleteProcessor(mContext, event));
    }

    @Subscribe
    public void onInterpretationTextUpdateEvent(InterpretationUpdateTextEvent event) {
        executeTask(new InterpretationUpdateTextProcessor(mContext, event));
    }

    @Subscribe
    public void onDatasetSyncEvent(DatasetSyncEvent event) {
        executeTask(new DatasetSyncProcessor(mContext));
    }

    @Subscribe
    public void onCreateReportEvent(ReportCreateEvent event) {
        executeTask(new ReportCreateProcessor(mContext, event));
    }

    @Subscribe
    public void onFieldValueChangeEvent(FieldValueChangeEvent event) {
        executeTask(new FieldChangeValueProcessor(mContext, event));
    }

    @Subscribe
    public void onReportDeleteEvent(ReportDeleteEvent event) {
        executeTask(new ReportDeleteProcessor(mContext, event));
    }

    @Subscribe
    public void onReportPostEvent(ReportPostEvent event) {
        executeTask(new ReportPostProcessor(mContext, event));
    }

    @Subscribe
    public void onUserAccountUpdateEvent(UserAccountUpdateEvent event) {
        executeTask(new UserAccountUpdateProcessor(mContext, event));
    }

    private <T> void executeTask(AsyncTask<Void, Void, T> task) {
        Log.d(TAG, "Starting: " + task.getClass().getSimpleName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
