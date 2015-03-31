package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.ui.views.CardDetailedButtonOverflow;

import java.util.List;

public class ReportListAdapter extends DBBaseAdapter<Report> {
    private OnItemClickListener mListener;
    private ReportState mState;

    public ReportListAdapter(Context context) {
        super(context);
        //mState = isNull(state, "ReportState object must not be null");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        StringHolder holder;

        if (convertView == null) {
            holder = new StringHolder(
                    getContext().getString(R.string.organization_unit),
                    getContext().getString(R.string.dataset),
                    getContext().getString(R.string.period)
            );
            CardDetailedButtonOverflow card = new CardDetailedButtonOverflow(getContext());
            card.addMenuItem(12, 12, R.string.delete);
            card.setTag(holder);
            view = card;
        } else {
            view = convertView;
            holder = (StringHolder) view.getTag();
        }

        handleView((CardDetailedButtonOverflow) view, holder, getItem(position));
        return view;
    }

    public DbRow<Report> getItem(int position) {
        List<DbRow<Report>> reports = getItems();
        DbRow<Report> report = null;
        if (reports != null && reports.size() > position) {
            report = reports.get(position);
        }
        return report;
    }

    private void handleView(final CardDetailedButtonOverflow button,
                            final StringHolder holder,
                            final DbRow<Report> dbRow) {
        if (dbRow != null && dbRow.getItem() != null) {
            Report report = dbRow.getItem();

            String orgUnit = holder.getOrgUnit() + ": " + report.getOrgUnitLabel();
            String dataSet = holder.getDataSet() + ": " + report.getDataSetLabel();
            String period = holder.getPeriod() + ": " + report.getPeriodLabel();

            button.setFirstLineText(orgUnit);
            button.setSecondLineText(dataSet);
            button.setThirdLineText(period);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(dbRow);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private static class StringHolder {
        private final String orgUnit;
        private final String dataSet;
        private final String period;

        private StringHolder(String orgUnit,
                             String dataSet,
                             String period) {
            this.orgUnit = orgUnit;
            this.dataSet = dataSet;
            this.period = period;
        }

        public String getOrgUnit() {
            return orgUnit;
        }

        public String getDataSet() {
            return dataSet;
        }

        public String getPeriod() {
            return period;
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(DbRow<Report> report);
    }
}
