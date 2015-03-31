package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.FieldValueChangeEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroups;
import org.hisp.dhis.mobile.datacapture.io.handlers.OptionSetHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportFieldHandler;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.adapters.FieldAdapter;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.AutoCompleteRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.CheckBoxRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.DatePickerRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.OnFieldValueSetListener;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RadioButtonsRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.Row;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RowTypes;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.ValueEntryViewRow;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.utils.DbUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReportGroupFragment extends Fragment
        implements LoaderCallbacks<List<Row>> {
    private static final int LOADER_ID = 438915134;
    private ListView mListView;
    private FieldAdapter mAdapter;

    public static ReportGroupFragment newInstance(int groupId) {
        ReportGroupFragment fragment = new ReportGroupFragment();
        Bundle args = new Bundle();
        args.putInt(ReportGroups.DB_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new FieldAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<Row>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            String groupId = args.getInt(ReportGroups.DB_ID) + "";
            System.out.println("GroupID: " + groupId);
            return CursorLoaderBuilder.forUri(ReportFields.CONTENT_URI)
                    .projection(ReportFieldHandler.PROJECTION)
                    .selection(ReportFieldHandler.SELECTION)
                    .selectionArgs(new String[]{groupId})
                    .transformation(new Transformer())
                    .build(getActivity().getBaseContext());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Row>> loader,
                               List<Row> data) {
        if (loader != null && loader.getId() == LOADER_ID
                && data != null) {
            mAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Row>> loader) {
    }

    private static class Transformer implements Transformation<List<Row>> {

        @Override
        public List<Row> transform(Context context, Cursor cursor) {
            List<DbRow<Field>> fields = ReportFieldHandler.map(cursor, false);
            List<Row> rows = new ArrayList<>();

            OnValueChangedListener listener = new OnValueChangedListener(context);
            OptionSetHandler optionSetHandler = new OptionSetHandler(context);

            Collections.sort(fields, new FieldComparator());
            for (DbRow<Field> dbItem : fields) {
                Field field = dbItem.getItem();

                Row row = null;
                if (field.getOptionSet() != null) {
                    OptionSet optionSet = readOptionSet(optionSetHandler,
                            field.getOptionSet());
                    row = new AutoCompleteRow(dbItem, optionSet);
                } else if (RowTypes.TEXT.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.TEXT);
                } else if (RowTypes.LONG_TEXT.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.LONG_TEXT);
                } else if (RowTypes.NUMBER.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.NUMBER);
                } else if (RowTypes.INTEGER.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER);
                } else if (RowTypes.INTEGER_NEGATIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_NEGATIVE);
                } else if (RowTypes.INTEGER_ZERO_OR_POSITIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_ZERO_OR_POSITIVE);
                } else if (RowTypes.INTEGER_POSITIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_POSITIVE);
                } else if (RowTypes.BOOLEAN.name().equals(field.getType())) {
                    row = new RadioButtonsRow(dbItem, RowTypes.BOOLEAN);
                } else if (RowTypes.GENDER.name().equals(field.getType())) {
                    row = new RadioButtonsRow(dbItem, RowTypes.GENDER);
                } else if (RowTypes.TRUE_ONLY.name().equals(field.getType())) {
                    row = new CheckBoxRow(dbItem);
                } else if (RowTypes.DATE.name().equals(field.getType())) {
                    row = new DatePickerRow(dbItem);
                }

                if (row != null) {
                    row.setListener(listener);
                    rows.add(row);
                }
            }
            return rows;
        }

        private OptionSet readOptionSet(OptionSetHandler handler, String optionSetId) {
            return DbUtils.stripRow(handler.query(optionSetId, true));
        }
    }

    private static final class FieldComparator implements Comparator<DbRow<Field>> {

        @Override
        public int compare(DbRow<Field> first, DbRow<Field> second) {
            if (first == null || second == null ||
                    first.getItem() == null || second.getItem() == null) {
                return 0;
            }

            String elementFirst = first.getItem().getDataElement();
            String elementSecond = second.getItem().getDataElement();
            if (equal(elementFirst, elementSecond)) {
                return 0;
            } else {
                return 1;
            }
        }

        private static boolean equal(String first, String second) {
            return (first == null ? second == null : first.equals(second));
        }
    }

    private static class OnValueChangedListener implements OnFieldValueSetListener {
        private Context context;

        public OnValueChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public void onFieldValueSet(int fieldId, String value) {
            FieldValueChangeEvent event = new FieldValueChangeEvent();
            event.setFieldId(fieldId);
            event.setValue(value);
            BusProvider.getInstance().post(event);
            Toast.makeText(context, "posting event", Toast.LENGTH_SHORT).show();
        }
    }
}