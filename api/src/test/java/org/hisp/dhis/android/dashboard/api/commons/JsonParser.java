package org.hisp.dhis.android.dashboard.api.commons;

import org.hisp.dhis.android.dashboard.api.utils.ObjectMapperProvider;

import java.io.IOException;

public class JsonParser {


    public static Object getModelFromJson(Class modelClass, String json) throws IOException {
        return ObjectMapperProvider.getInstance()
                .readValue(json, modelClass);
    }

}