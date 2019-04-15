package org.hisp.dhis.android.dashboard.api.controllers;

import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.merge;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

import com.raizlabs.android.dbflow.sql.language.Select;
import org.hisp.dhis.android.dashboard.api.models.DataMap;
import org.hisp.dhis.android.dashboard.api.models.meta.DbOperation;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.hisp.dhis.android.dashboard.api.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import okhttp3.HttpUrl;

public class MapController {
    private DhisApi mDhisApi;

    private static List<DataMap> mDataMaps;

    public MapController(DhisApi dhisApi) {
        mDhisApi = dhisApi;
    }

    public static List<DataMap> queryDataMaps() {
        return new Select().from(DataMap.class)
                .queryList();
    }

    public static DataMap getDataMap(String uid) {
        DataMap dataMapToReturn = null;

        if (mDataMaps == null)
            mDataMaps = queryDataMaps();

        for (DataMap dataMap:mDataMaps) {
            if (dataMap.getUId().equals(uid))
                dataMapToReturn = dataMap;
        }

        return dataMapToReturn;
    }

    public void syncDataMaps() throws APIException {
        getDataMapsFromServer();
    }

    private void getDataMapsFromServer() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);
        DateTime serverDateTime = mDhisApi.getSystemInfo()
                .getServerDate();

        List<DataMap> dataMaps = updateDataMaps(lastUpdated);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(queryDataMaps(), dataMaps));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS, serverDateTime);
    }

    private List<DataMap> updateDataMaps(DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access";

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", BASE + "basemap,latitude,longitude,zoom");

/*        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter",
                    "lastUpdated:gt:" + lastUpdated.toLocalDateTime().toString());
        }*/

        // List of dashboards with UUIDs (without content). This list is used
        // only to determine what was removed on server.
        List<DataMap> actualDataMaps = unwrapResponse(mDhisApi
                .getDataMaps(QUERY_MAP_BASIC), "maps");

        // List of updated dashboards with content.
        List<DataMap> updatedDataMaps = unwrapResponse(mDhisApi
                .getDataMaps(QUERY_MAP_FULL), "maps");

        // List of persisted dashboards.
        List<DataMap> persistedDataMaps = queryDataMaps();

        return merge(actualDataMaps, updatedDataMaps, persistedDataMaps);
    }

    private String getMapUIDs(String request) {
        HttpUrl url = HttpUrl.parse(request);

        String uid = url.pathSegments().get(3);

        return uid;
    }

}
