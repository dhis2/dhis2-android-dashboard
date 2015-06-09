package org.dhis2.android.dashboard.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by araz on 06.06.2015.
 */
public class NetworkUtils {
    private NetworkUtils() {
        // no instances
    }

    public static <T> List<T> unwrapResponse(Map<String, List<T>> response, String key) {
        if (response != null && response.containsKey(key) && response.get(key) != null) {
            return response.get(key);
        } else {
            return new ArrayList<>();
        }
    }
}
