package org.hisp.dhis.android.dashboard.api.controllers;

import android.content.Context;

import com.squareup.picasso.NetworkPolicy;

import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.utils.PicassoProvider;

import java.util.ArrayList;
import java.util.List;


final class PullImageController {

    static Context mContext;

    public PullImageController(Context context) {
        mContext = context;
    }

    public void pullDashboardImages(DhisController.ImageNetworkPolicy imageNetworkPolicy) throws APIException {
        List<String> requestList = new ArrayList<>();
        requestList = downloadDashboardImages(requestList);
        downloadImages(imageNetworkPolicy, requestList, mContext);
    }

    public void pullInterpretationImages(DhisController.ImageNetworkPolicy imageNetworkPolicy) throws APIException {
        List<String> requestList = new ArrayList<>();
        requestList = downloadInterpretationImages(requestList);
        downloadImages(imageNetworkPolicy, requestList, mContext);
    }

    public static List<String> downloadInterpretationImages(List<String> requestList) {
        for (InterpretationElement interpretationElement : InterpretationController
                .queryAllInterpretationElements()) {
            if (interpretationElement == null || interpretationElement.getType() == null) {
                continue;
            }
            if (Interpretation.TYPE_CHART.equals(interpretationElement.getType())) {
                requestList.add(
                        DhisController.buildImageUrl("charts", interpretationElement.getUId(),
                                mContext));
            } else if (Interpretation.TYPE_MAP.equals(interpretationElement.getType())) {
                requestList.add(DhisController.buildImageUrl("maps", interpretationElement.getUId(),
                        mContext));
            }
        }
        return requestList;
    }

    public static List<String> downloadDashboardImages(List<String> requestList) {
        for (DashboardElement element : DashboardController.queryAllDashboardElement()) {
            if (element.getDashboardItem() == null
                    || element.getDashboardItem().getType() == null) {
                continue;
            }

            switch (element.getDashboardItem().getType()) {
                case DashboardItemContent.TYPE_CHART: {
                    requestList.add(
                            DhisController.buildImageUrl("charts", element.getUId(), mContext));
                    break;
                }
                case DashboardItemContent.TYPE_EVENT_CHART: {
                    requestList.add(DhisController.buildImageUrl("eventCharts", element.getUId(),
                            mContext));
                    break;
                }
                case DashboardItemContent.TYPE_MAP: {
                    requestList.add(
                            DhisController.buildImageUrl("maps", element.getUId(), mContext));
                    break;
                }
            }
        }
        return requestList;
    }

    private static void downloadImages(DhisController.ImageNetworkPolicy imageNetworkPolicy,
            final List<String> requestUrlList, final Context context) {
        for (int i = 0; i < requestUrlList.size(); i++) {
            final String request = requestUrlList.get(i);

            if (imageNetworkPolicy == DhisController.ImageNetworkPolicy.NO_CACHE) {
                PicassoProvider.getInstance(context)
                        .load(request).networkPolicy(NetworkPolicy.NO_CACHE).fetch();
            } else {
                PicassoProvider.getInstance(context)
                        .load(request).fetch();
            }
        }
    }
}
