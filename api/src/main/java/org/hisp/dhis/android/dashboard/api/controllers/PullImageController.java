package org.hisp.dhis.android.dashboard.api.controllers;

import android.content.Context;

import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.utils.PicassoProvider;

import java.util.ArrayList;
import java.util.List;

final class PullImageController {
    private final DhisApi mDhisApi;
    Context mContext;

    private static String MAPS_ENDPOINT = "maps";
    private static String CHARTS_ENDPOINT = "charts";
    private static String EVENT_CHARTS_ENDPOINT = "eventCharts";


    public PullImageController(DhisApi dhisApi, Context context) {
        mDhisApi = dhisApi;
        mContext = context;
    }

    public void pullDashboardImages() throws APIException {
        List<String> requestList = new ArrayList<>();
        requestList = downloadDashboardImages(requestList);
        downloadImages(requestList);
    }

    public void pullInterpretationImages() throws APIException {
        List<String> requestList = new ArrayList<>();
        requestList = downloadInterpretationImages(requestList);
        downloadImages(requestList);
    }

    public List<String> downloadInterpretationImages(List<String> requestList) {
        for (InterpretationElement interpretationElement : InterpretationController
                .queryAllInterpretationElements()) {
            if (interpretationElement == null || interpretationElement.getType() == null) {
                continue;
            }
            if (Interpretation.TYPE_CHART.equals(interpretationElement.getType())) {
                requestList.add(
                        DhisController.buildImageUrl(CHARTS_ENDPOINT,
                                interpretationElement.getUId()));
            } else if (Interpretation.TYPE_MAP.equals(interpretationElement.getType())) {
                requestList.add(
                        DhisController.buildImageUrl(MAPS_ENDPOINT,
                                interpretationElement.getUId()));
            }
        }
        return requestList;
    }

    public List<String> downloadDashboardImages(List<String> requestList) {
        for (DashboardElement element : DashboardController.queryAllDashboardElement()) {
            if (element.getDashboardItem().getType() == null) {
                continue;
            }

            switch (element.getDashboardItem().getType()) {
                case DashboardItemContent.TYPE_CHART: {
                    requestList.add(
                            DhisController.buildImageUrl(CHARTS_ENDPOINT, element.getUId()));
                    break;
                }
                case DashboardItemContent.TYPE_EVENT_CHART: {
                    requestList.add(
                            DhisController.buildImageUrl(EVENT_CHARTS_ENDPOINT, element.getUId()));
                    break;
                }
                case DashboardItemContent.TYPE_MAP: {
                    requestList.add(DhisController.buildImageUrl(MAPS_ENDPOINT, element.getUId()));
                    break;
                }
            }
        }
        return requestList;
    }

    private void downloadImages(final List<String> requestUrlList) {
        MapController mapController = new MapController(mDhisApi, mContext);

        for (int i = 0; i < requestUrlList.size(); i++) {
            final String request = requestUrlList.get(i);

            if (request.contains(MAPS_ENDPOINT)) {
                mapController.downloadImageMap(request);
            } else {
                PicassoProvider.getInstance(mContext)
                        .load(request)
                        .fetch();
            }
        }
    }


}
