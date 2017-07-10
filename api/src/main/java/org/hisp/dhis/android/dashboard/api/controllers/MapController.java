package org.hisp.dhis.android.dashboard.api.controllers;

import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.toMap;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

import android.content.Context;
import android.net.Uri;

import com.squareup.okhttp.HttpUrl;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.hisp.dhis.android.dashboard.api.models.DataMap;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.BaseMapLayerDhisTransformation;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.utils.PicassoProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapController {
    private Context mContext;
    private DhisApi mDhisApi;

    public MapController(DhisApi dhisApi, Context context) {
        mDhisApi = dhisApi;
        mContext = context;
    }

    public void downloadImageMap(String request) {
        final Map<String, DataMap> dataMaps = getDataMaps();

        Picasso picasso = PicassoProvider.createNewInstance(mContext, new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                DataMap dataMap = dataMaps.get(getMapUIDs(uri.toString()));

                picasso.load(uri.toString())
                        .transform(new BaseMapLayerDhisTransformation(mContext, dataMap))
                        .networkPolicy(NetworkPolicy.NO_CACHE).fetch();
            }
        });

        picasso.load(request)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fetch();
    }

    private Map<String, DataMap> getDataMaps() throws APIException {
        return getDataMaps(null);
    }

    private Map<String, DataMap> getDataMaps(List<String> uids) throws APIException {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,basemap,latitude,longitude,zoom");

        if (uids != null) {
            QUERY_PARAMS.put("filter", "id:in:[" + android.text.TextUtils.join(",", uids) + "]");
        }

        List<DataMap> mapList = unwrapResponse(mDhisApi.getDataMaps(QUERY_PARAMS), "maps");

        Map<String, DataMap> maps = toMap(mapList);

        return maps;
    }

    private String getMapUIDs(String request) {
        HttpUrl url = HttpUrl.parse(request);

        String uid = url.pathSegments().get(3);

        return uid;
    }

}
