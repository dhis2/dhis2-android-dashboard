package org.hisp.dhis.android.core.dashboard;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DashboardTests {
    private final String DASHBOARD_JSON = "[{\n"
            + "\t\t\"lastUpdated\": \"2016-10-11T19:24:33.599\",\n"
            + "\t\t\"created\": \"2013-09-08T21:47:17.960\",\n"
            + "\t\t\"name\": \"Antenatal Care\",\n"
            + "\t\t\"id\": \"nghVC4wtyzi\",\n"
            + "\t\t\"displayName\": \"Antenatal Care\",\n"
            + "\t\t\"access\": {\n"
            + "\t\t\t\"read\": true,\n"
            + "\t\t\t\"update\": true,\n"
            + "\t\t\t\"externalize\": true,\n"
            + "\t\t\t\"delete\": true,\n"
            + "\t\t\t\"write\": true,\n"
            + "\t\t\t\"manage\": true\n"
            + "\t\t},\n"
            + "\t\t\"dashboardItems\": [{\n"
            + "\t\t\t\"created\": \"2016-10-10T17:24:30.487\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-10-10T17:24:30.487\",\n"
            + "\t\t\t\"id\": \"cX2przhv9UC\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2016-10-10T17:24:49.196\",\n"
            + "\t\t\t\t\"created\": \"2016-10-10T17:24:49.196\",\n"
            + "\t\t\t\t\"name\": \"ANC: ANC IPT 1 Coverage last 12 months districts\",\n"
            + "\t\t\t\t\"id\": \"VffWmdKFHSq\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: ANC IPT 1 Coverage last 12 months districts\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2016-08-02T11:57:55.229\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-08-02T11:57:55.229\",\n"
            + "\t\t\t\"id\": \"JcO7yJlKIa3\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2016-10-10T17:08:15.277\",\n"
            + "\t\t\t\t\"created\": \"2016-10-10T17:08:15.277\",\n"
            + "\t\t\t\t\"name\": \"ANC: ANC 3 coverage by districts last 4 quarters\",\n"
            + "\t\t\t\t\"id\": \"CNkMibmx1Zr\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: ANC 3 coverage by districts last 4 quarters\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2015-01-16T11:52:44.928\",\n"
            + "\t\t\t\"lastUpdated\": \"2015-01-16T11:52:44.928\",\n"
            + "\t\t\t\"id\": \"OiyMNoXzSdY\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"MAP\",\n"
            + "\t\t\t\"map\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2013-09-09T16:35:12.062\",\n"
            + "\t\t\t\t\"created\": \"2012-11-14T12:56:59.322\",\n"
            + "\t\t\t\t\"name\": \"ANC: LLITN coverage district and facility\",\n"
            + "\t\t\t\t\"id\": \"ZBjCfSaLSqD\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: LLITN coverage district and facility\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2016-04-21T15:37:07.740\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-04-21T15:37:07.740\",\n"
            + "\t\t\t\"id\": \"i6NTSuDsk6l\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"MAP\",\n"
            + "\t\t\t\"map\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2016-10-10T18:16:43.265\",\n"
            + "\t\t\t\t\"created\": \"2016-10-10T18:16:43.261\",\n"
            + "\t\t\t\t\"name\": \"ANC: IPT 2 Coverage this year\",\n"
            + "\t\t\t\t\"id\": \"voX07ulo2Bq\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: IPT 2 Coverage this year\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2015-01-15T16:50:51.427\",\n"
            + "\t\t\t\"lastUpdated\": \"2015-08-09T22:10:20.307\",\n"
            + "\t\t\t\"id\": \"YZ7U25Japom\",\n"
            + "\t\t\t\"shape\": \"DOUBLE_WIDTH\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2015-07-15T15:25:20.004\",\n"
            + "\t\t\t\t\"created\": \"2015-01-15T16:50:34.302\",\n"
            + "\t\t\t\t\"name\": \"ANC: ANC 1 coverage western chiefdoms this year\",\n"
            + "\t\t\t\t\"id\": \"zKl0LcQyxPl\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: ANC 1 coverage western chiefdoms this year\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2016-08-02T11:57:59.474\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-10-10T17:11:06.823\",\n"
            + "\t\t\t\"id\": \"UQeYhQOJ2f1\",\n"
            + "\t\t\t\"shape\": \"DOUBLE_WIDTH\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2016-08-02T11:58:28.517\",\n"
            + "\t\t\t\t\"created\": \"2016-08-02T11:53:53.607\",\n"
            + "\t\t\t\t\"name\": \"ANC: IPT 1 Coverage by districts last 4 quarters\",\n"
            + "\t\t\t\t\"id\": \"DHPu0vtZ2mW\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: IPT 1 Coverage by districts last 4 quarters\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2014-04-03T14:11:10.942\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-10-11T09:29:40.874\",\n"
            + "\t\t\t\"id\": \"xS4X0ZL6GCI\",\n"
            + "\t\t\t\"shape\": \"DOUBLE_WIDTH\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2015-07-15T15:25:20.312\",\n"
            + "\t\t\t\t\"created\": \"2014-04-03T14:07:29.442\",\n"
            + "\t\t\t\t\"name\": \"ANC: Fixed vs Outreach last year\",\n"
            + "\t\t\t\t\"id\": \"AVZpYsdG44G\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: Fixed vs Outreach last year\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2016-10-11T19:24:21.931\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-10-11T19:24:21.931\",\n"
            + "\t\t\t\"id\": \"ZF9vWMXob7N\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"MAP\",\n"
            + "\t\t\t\"map\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2016-10-11T19:23:49.953\",\n"
            + "\t\t\t\t\"created\": \"2016-10-11T19:23:49.952\",\n"
            + "\t\t\t\t\"name\": \"ANC: ANC 1 coverage Sierra Leone dark basemap\",\n"
            + "\t\t\t\t\"id\": \"qTfO4YkQ9xW\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: ANC 1 coverage Sierra Leone dark basemap\"\n"
            + "\t\t\t}\n"
            + "\t\t}, {\n"
            + "\t\t\t\"created\": \"2014-04-03T14:09:47.075\",\n"
            + "\t\t\t\"lastUpdated\": \"2016-10-11T09:19:35.442\",\n"
            + "\t\t\t\"id\": \"kHRSFUr3dYe\",\n"
            + "\t\t\t\"shape\": \"NORMAL\",\n"
            + "\t\t\t\"type\": \"CHART\",\n"
            + "\t\t\t\"chart\": {\n"
            + "\t\t\t\t\"lastUpdated\": \"2015-07-15T15:25:20.315\",\n"
            + "\t\t\t\t\"created\": \"2014-04-03T14:09:05.734\",\n"
            + "\t\t\t\t\"name\": \"ANC: 4+ visits by Facility Type last year\",\n"
            + "\t\t\t\t\"id\": \"ZfQMIA4o2s3\",\n"
            + "\t\t\t\t\"displayName\": \"ANC: 4+ visits by Facility Type last year\"\n"
            + "\t\t\t}\n"
            + "\t\t}]\n"
            + "\t}]";

    @Test
    public void dashboard_shouldMapFromJsonString() throws IOException {
        Dashboard dashboard = getDashboardsFromJson();
        assertTrue(dashboard.getState().equals(State.SYNCED));
        assertTrue(dashboard.getUId().equals("nghVC4wtyzi"));
        assertTrue(dashboard.getName().equals("Antenatal Care"));
        assertTrue(dashboard.getDisplayName().equals("Antenatal Care"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboard.getCreated(),
                "2013-09-08T21:47:17.960"));
        assertTrue(dashboard.getDashboardItems().size() == 9);
    }

    @Test
    public void dashboard_access_shouldMapFromJsonString() throws IOException {
        Dashboard dashboard = getDashboardsFromJson();
        Access access = dashboard.getAccess();
        assertTrue(access.isManage() == true);
        assertTrue(access.isExternalize() == true);
        assertTrue(access.isWrite() == true);
        assertTrue(access.isRead() == true);
        assertTrue(access.isUpdate() == true);
        assertTrue(access.isDelete() == true);
    }

    @Test
    public void dashboard_item_type_chart_shouldMapFromJsonString() throws IOException {
        DashboardItem dashboardItem = getDashboardItemWithChart();
        assertTrue(dashboardItem.getName() == null);
        assertTrue(dashboardItem.getDisplayName() == null);
        assertTrue(dashboardItem.getState() == State.SYNCED);
        assertTrue(dashboardItem.getEventChart() == null);
        assertTrue(dashboardItem.getEventReport() == null);
        assertTrue(dashboardItem.getUsers() == null);
        assertTrue(dashboardItem.getReports() == null);
        assertTrue(dashboardItem.getResources() == null);
        assertTrue(dashboardItem.isMessages() == false);
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardItem.getCreated(),
                "2016-10-10T17:24:30.487"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardItem.getLastUpdated(),
                "2016-10-10T17:24:30.487"));
        assertTrue(dashboardItem.getUId().equals("cX2przhv9UC"));
        assertTrue(dashboardItem.getShape().equals("NORMAL"));
        assertTrue(dashboardItem.getType().equals("CHART"));
        assertTrue(dashboardItem.getAccess() == null);

        DashboardElement dashboardElement = dashboardItem.getChart();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardElement.getLastUpdated(),
                "2016-10-10T17:24:49.196"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardElement.getCreated(),
                "2016-10-10T17:24:49.196"));
        assertTrue(dashboardElement.getName().equals(
                "ANC: ANC IPT 1 Coverage last 12 months districts"));
        assertTrue(dashboardElement.getUId().equals("VffWmdKFHSq"));
        assertTrue(dashboardElement.getDisplayName().equals(
                "ANC: ANC IPT 1 Coverage last 12 months districts"));
    }

    @Test
    public void dashboard_item_type_map_shouldMapFromJsonString() throws IOException {
        DashboardItem dashboardItem = getDashboardItemWithMap();
        assertTrue(dashboardItem.getName() == null);
        assertTrue(dashboardItem.getDisplayName() == null);
        assertTrue(dashboardItem.getState() == State.SYNCED);
        assertTrue(dashboardItem.getEventChart() == null);
        assertTrue(dashboardItem.getEventReport() == null);
        assertTrue(dashboardItem.getUsers() == null);
        assertTrue(dashboardItem.getReports() == null);
        assertTrue(dashboardItem.getResources() == null);
        assertTrue(dashboardItem.isMessages() == false);
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardItem.getCreated(),
                "2015-01-16T11:52:44.928"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardItem.getLastUpdated(),
                "2015-01-16T11:52:44.928"));
        assertTrue(dashboardItem.getUId().equals("OiyMNoXzSdY"));
        assertTrue(dashboardItem.getShape().equals("NORMAL"));
        assertTrue(dashboardItem.getType().equals("MAP"));
        assertTrue(dashboardItem.getAccess() == null);
        DashboardElement dashboardElement = dashboardItem.getMap();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardElement.getLastUpdated(),
                "2013-09-09T16:35:12.062"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dashboardElement.getCreated(),
                "2012-11-14T12:56:59.322"));
        assertTrue(dashboardElement.getName().equals("ANC: LLITN coverage district and facility"));
        assertTrue(dashboardElement.getUId().equals("ZBjCfSaLSqD"));
        assertTrue(dashboardElement.getDisplayName().equals(
                "ANC: LLITN coverage district and facility"));
    }

    private DashboardItem getDashboardItemWithChart() throws IOException {
        Dashboard dashboard = getDashboardsFromJson();
        List<DashboardItem> dashboardItems = dashboard.getDashboardItems();
        return dashboardItems.get(0);
    }

    private DashboardItem getDashboardItemWithMap() throws IOException {
        Dashboard dashboard = getDashboardsFromJson();
        List<DashboardItem> dashboardItems = dashboard.getDashboardItems();
        return dashboardItems.get(2);
    }

    private Dashboard getDashboardsFromJson() throws IOException {
        Dashboard[] dashboards = (Dashboard[]) JsonParser.getModelFromJson(Dashboard[].class,
                DASHBOARD_JSON);
        return dashboards[0];
    }

}
