package org.hisp.dhis.android.dashboard.api.converters;

import static junit.framework.Assert.assertTrue;

import org.hisp.dhis.android.dashboard.api.commons.FileReader;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.persistence.converters.AccessConverter;
import org.junit.Test;

import java.io.IOException;

public class AccessConverterTests {
    public static final String ACCESS_ALL_FALSE_STRING_TXT = "access_all_false_string.json";
    public static final String ACCESS_ALL_TRUE_STRING_TXT = "access_all_true_string.json";
    AccessConverter accessConverter = new AccessConverter();

    @Test
    public void convert_access_object_to_database_string() throws Exception {
        String access = getAccessFromJson(ACCESS_ALL_TRUE_STRING_TXT);
        assertTrue(accessConverter.getDBValue(Access.provideDefaultAccess()).equals(access));
    }

    @Test
    public void convert_access_all_true_database_string_to_model() throws IOException {
        Access access = accessConverter.getModelValue(
                getAccessFromJson(ACCESS_ALL_TRUE_STRING_TXT));
        assertTrue(access.isDelete());
        assertTrue(access.isRead());
        assertTrue(access.isWrite());
        assertTrue(access.isManage());
        assertTrue(access.isExternalize());
    }

    @Test
    public void convert_access_all_false_database_string_to_model() throws IOException {
        Access access = accessConverter.getModelValue(
                getAccessFromJson(ACCESS_ALL_FALSE_STRING_TXT));
        assertTrue(!access.isDelete());
        assertTrue(!access.isRead());
        assertTrue(!access.isWrite());
        assertTrue(!access.isManage());
        assertTrue(!access.isExternalize());
    }

    private String getAccessFromJson(String json) throws IOException {
        return new FileReader().getStringFromFile(json).replaceAll(" ", "").replace("\t", "");
    }
}
