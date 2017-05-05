package org.hisp.dhis.android.core.dashboard;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.FileReader;
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
                new FileReader().getStringFromFile("dashboard.json"));
        return dashboards[0];
    }

}
