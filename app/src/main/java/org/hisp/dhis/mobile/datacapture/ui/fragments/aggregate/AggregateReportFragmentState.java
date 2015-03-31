package org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate;

import android.os.Parcel;
import android.os.Parcelable;

import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;

class AggregateReportFragmentState implements Parcelable {
    private static final String TAG = AggregateReportFragmentState.class.getName();
    private static final int DEFAULT_INDEX = -1;
    private boolean syncInProcess;

    private String orgUnitLabel;
    private String orgUnitId;
    private int orgUnitDBId;

    private String dataSetLabel;
    private String dataSetId;
    private int dataSetDBId;

    private String periodLabel;
    private String periodDate;

    public AggregateReportFragmentState() {
    }

    public AggregateReportFragmentState(AggregateReportFragmentState state) {
        if (state != null) {
            setSyncInProcess(state.isSyncInProcess());
            setOrgUnit(state.getOrgUnitDBId(), state.getOrgUnitId(),
                    state.getOrgUnitLabel());
            setDataSet(state.getDataSetDBId(), state.getDataSetId(),
                    state.getDataSetLabel());
            setPeriod(state.getPeriod());
        }
    }

    public static final Parcelable.Creator<AggregateReportFragmentState> CREATOR
            = new Parcelable.Creator<AggregateReportFragmentState>() {

        public AggregateReportFragmentState createFromParcel(Parcel in) {
            return new AggregateReportFragmentState(in);
        }

        public AggregateReportFragmentState[] newArray(int size) {
            return new AggregateReportFragmentState[size];
        }
    };

    private AggregateReportFragmentState(Parcel in) {
        syncInProcess = in.readInt() == 1;

        orgUnitLabel = in.readString();
        orgUnitId = in.readString();
        orgUnitDBId = in.readInt();

        dataSetLabel = in.readString();
        dataSetId = in.readString();
        dataSetDBId = in.readInt();

        periodLabel = in.readString();
        periodDate = in.readString();
    }

    @Override
    public int describeContents() {
        return TAG.length();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(syncInProcess ? 1 : 0);

        parcel.writeString(orgUnitLabel);
        parcel.writeString(orgUnitId);
        parcel.writeInt(orgUnitDBId);

        parcel.writeString(dataSetLabel);
        parcel.writeString(dataSetId);
        parcel.writeInt(dataSetDBId);

        parcel.writeString(periodLabel);
        parcel.writeString(periodDate);
    }

    public boolean isSyncInProcess() {
        return syncInProcess;
    }

    public void setSyncInProcess(boolean syncInProcess) {
        this.syncInProcess = syncInProcess;
    }

    public void setOrgUnit(int orgUnitDBId, String orgUnitId,
                           String orgUnitLabel) {
        this.orgUnitDBId = orgUnitDBId;
        this.orgUnitId = orgUnitId;
        this.orgUnitLabel = orgUnitLabel;
    }

    public void resetOrgUnit() {
        orgUnitDBId = DEFAULT_INDEX;
        orgUnitId = null;
        orgUnitLabel = null;
    }

    public boolean isOrgUnitEmpty() {
        return (orgUnitDBId == DEFAULT_INDEX ||
                orgUnitId == null || orgUnitLabel == null);
    }

    public String getOrgUnitLabel() {
        return orgUnitLabel;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public int getOrgUnitDBId() {
        return orgUnitDBId;
    }

    public void setDataSet(int dataSetDBId, String dataSetId,
                           String dataSetLabel) {
        this.dataSetDBId = dataSetDBId;
        this.dataSetId = dataSetId;
        this.dataSetLabel = dataSetLabel;
    }

    public void resetDataSet() {
        dataSetDBId = DEFAULT_INDEX;
        dataSetId = null;
        dataSetLabel = null;
    }

    public boolean isDataSetEmpty() {
        return (dataSetDBId == DEFAULT_INDEX ||
                dataSetId == null || dataSetLabel == null);
    }

    public String getDataSetLabel() {
        return dataSetLabel;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public int getDataSetDBId() {
        return dataSetDBId;
    }

    public DateHolder getPeriod() {
        return new DateHolder(periodDate, periodLabel);
    }

    public void setPeriod(DateHolder dateHolder) {
        if (dateHolder != null) {
            periodLabel = dateHolder.getLabel();
            periodDate = dateHolder.getDate();
        }
    }

    public void resetPeriod() {
        periodLabel = null;
        periodDate = null;
    }

    public boolean isPeriodEmpty() {
        return (periodLabel == null || periodDate == null);
    }
}