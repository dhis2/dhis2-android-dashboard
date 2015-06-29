/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.utils;

import org.dhis2.android.dashboard.api.models.BaseIdentifiableObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.utils.CollectionUtils.toMap;

public final class MergeUtils {
    private MergeUtils() {
        // no instances
    }

    public static <T extends BaseIdentifiableObject> List<T> merge(List<T> existingItems,
                                                                   List<T> updatedItems,
                                                                   List<T> persistedItems) {
        Map<String, T> updatedItemsMap = toMap(updatedItems);
        Map<String, T> persistedItemsMap = toMap(persistedItems);
        Map<String, T> existingItemsMap = new HashMap<>();

        if (existingItems == null || existingItems.isEmpty()) {
            return new ArrayList<>(existingItemsMap.values());
        }

        for (T existingItem : existingItems) {
            String id = existingItem.getUId();
            T updatedItem = updatedItemsMap.get(id);
            T persistedItem = persistedItemsMap.get(id);

            if (updatedItem != null) {
                if (persistedItem != null) {
                    updatedItem.setId(persistedItem.getId());
                }
                existingItemsMap.put(id, updatedItem);
                continue;
            }

            if (persistedItem != null) {
                existingItemsMap.put(id, persistedItem);
            }
        }

        return new ArrayList<>(existingItemsMap.values());
    }
}
