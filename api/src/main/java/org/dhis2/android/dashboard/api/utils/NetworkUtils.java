/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;

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

    public static boolean isSuccess(int status) {
        return status >= 200 && status < 300;
    }

    public static Header findLocationHeader(List<Header> headers) {
        final String LOCATION = "location";
        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase(LOCATION)) {
                    return header;
                }
            }
        }

        return null;
    }
}
