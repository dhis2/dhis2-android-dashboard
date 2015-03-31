package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.OptionSetHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.OrganizationUnitHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DatasetSyncProcessor extends AbsProcessor<DashboardSyncEvent, OnDatasetSyncEvent> {

    public DatasetSyncProcessor(Context context) {
        super(context);
    }

    @Override
    public OnDatasetSyncEvent process() {
        final OnDatasetSyncEvent event = new OnDatasetSyncEvent();
        final ResponseHolder<String> holder = new ResponseHolder<>();

        try {
            updateDataSets();
        } catch (APIException e) {
            holder.setException(e);
        }

        event.setResponseHolder(holder);
        return event;
    }

    private void updateDataSets() throws APIException {
        OrganizationUnitHandler unitHandler =
                new OrganizationUnitHandler(getContext());
        OptionSetHandler optionSetHandler =
                new OptionSetHandler(getContext());

        // network operations should be done first, in order to make sure that
        // we have all data in hands before starting processing it
        DataSetHolder dataSetHolder = getDatasets();
        // We need to save OptionSets first,
        // since some fields of DataSets can reference them in DB
        List<OptionSet> optionSets = getOptionSets(dataSetHolder);
        optionSetHandler.bulkInsert(optionSets);

        List<OrganisationUnit> units = prepareUnits(dataSetHolder);
        unitHandler.bulkInsert(units);
    }

    private List<OrganisationUnit> prepareUnits(DataSetHolder dataSetHolder) {
        if (dataSetHolder == null ||
                dataSetHolder.getOrganisationUnits() == null ||
                dataSetHolder.getDataSets() == null) {
            return new ArrayList<>();
        }

        Collection<OrganisationUnit> units = dataSetHolder
                .getOrganisationUnits().values();
        Map<String, DataSet> dataSets = dataSetHolder.getDataSets();
        for (OrganisationUnit unit: units) {
            List<DataSet> fullDataSets = new ArrayList<>();
            for (DataSet shortDataSet: unit.getDataSets()) {
                DataSet fullDataSet = dataSets.get(shortDataSet.getId());
                fullDataSet.setId(shortDataSet.getId());
                fullDataSets.add(fullDataSet);
            }
            unit.setDataSets(fullDataSets);
        }

        return new ArrayList<>(units);
    }

    private List<OptionSet> getOptionSets(DataSetHolder holder) throws APIException {
        List<OptionSet> optionSets = new ArrayList<>();
        Set<String> optionSetIds = new HashSet<>();

        if (holder.getDataSets() == null ||
                !(holder.getDataSets().size() > 0)) {
            return optionSets;
        }

        Collection<DataSet> dataSets = holder.getDataSets().values();
        for (DataSet dataSet : dataSets) {

            Collection<Group> groups = dataSet.getGroups();
            for (Group group : groups) {

                Collection<Field> fields = group.getFields();
                for (Field field : fields) {

                    if (field.getOptionSet() != null) {
                        optionSetIds.add(field.getOptionSet());
                    }
                }
            }
        }

        for (String optionSetId : optionSetIds) {
            OptionSet optionSet = getOptionSet(optionSetId);
            optionSets.add(optionSet);
        }

        return optionSets;
    }

    private DataSetHolder getDatasets() throws APIException {
        final ResponseHolder<DataSetHolder> holder = new ResponseHolder<>();

        DHISManager.getInstance().getDataSets(new ApiRequestCallback<DataSetHolder>() {
            @Override
            public void onSuccess(Response response, DataSetHolder dataSetHolder) {
                holder.setItem(dataSetHolder);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        });

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }

    private OptionSet getOptionSet(String optionSetId) throws APIException {
        final ResponseHolder<OptionSet> holder = new ResponseHolder<>();

        DHISManager.getInstance().getOptionSet(new ApiRequestCallback<OptionSet>() {
            @Override
            public void onSuccess(Response response, OptionSet optionSet) {
                holder.setItem(optionSet);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, optionSetId);

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }
}