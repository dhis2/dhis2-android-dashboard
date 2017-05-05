package org.hisp.dhis.android.core.systeminfo;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.SystemInfo;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class SystemInfoTests {
    private static final String SYSTEM_INFO_JSON = "{\n"
            + "\n"
            + "    \"contextPath\": \"https://play.dhis2.org/demo\",\n"
            + "    \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 "
            + "Firefox/53.0\",\n"
            + "    \"calendar\": \"iso8601\",\n"
            + "    \"dateFormat\": \"yyyy-mm-dd\",\n"
            + "    \"serverDate\": \"2017-05-04T16:34:45.957\",\n"
            + "    \"lastAnalyticsTableSuccess\": \"2017-01-26T23:19:34.009\",\n"
            + "    \"intervalSinceLastAnalyticsTableSuccess\": \"2345 h, 15 m, 11 s\",\n"
            + "    \"lastAnalyticsTableRuntime\": \"5 m, 17 s\",\n"
            + "    \"version\": \"2.26\",\n"
            + "    \"revision\": \"f297d4c\",\n"
            + "    \"buildTime\": \"2017-05-04T06:37:32.000\",\n"
            + "    \"jasperReportsVersion\": \"6.3.1\",\n"
            + "    \"environmentVariable\": \"DHIS2_HOME\",\n"
            + "    \"readOnlyMode\": \"off\",\n"
            + "    \"databaseInfo\": {\n"
            + "        \"type\": \"PostgreSQL\",\n"
            + "        \"spatialSupport\": true\n"
            + "    },\n"
            + "    \"encryption\": false,\n"
            + "    \"isMetadataVersionEnabled\": true,\n"
            + "    \"isMetadataSyncEnabled\": false\n"
            + "\n"
            + "}";

    @Test
    public void systemInfo_should_map_from_json_String() throws IOException, ParseException {

        SystemInfo systemInfo = (SystemInfo) JsonParser.getModelFromJson(SystemInfo.class,
                SYSTEM_INFO_JSON);

        assertTrue(systemInfo.getVersion().equals("2.26"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(systemInfo.getBuildTime(),
                "2017-05-04T06:37:32.000"));
        assertTrue(systemInfo.getCalendar().equals("iso8601"));
        assertTrue(systemInfo.getIntervalSinceLastAnalyticsTableSuccess().equals("2345 h, 15 m, 11 s"));
        assertTrue(systemInfo.getLastAnalyticsTableSuccess().equals("2017-01-26T23:19:34.009"));
        assertTrue(systemInfo.getRevision().equals("f297d4c"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(systemInfo.getServerDate(),
                "2017-05-04T16:34:45.957"));
        assertTrue(systemInfo.getDateFormat().equals("yyyy-mm-dd"));
    }


}
