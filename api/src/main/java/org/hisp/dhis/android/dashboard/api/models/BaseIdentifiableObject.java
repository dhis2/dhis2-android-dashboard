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

package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.dashboard.api.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseIdentifiableObject extends BaseModel implements IdentifiableObject {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIMESTAMP_PATTERN_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String TIMESATAM_PATTERN_TO_PRINT = "yyyy-MM-dd HH:mm";

    public static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(TIMESTAMP_PATTERN);

    @JsonIgnore
    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    long id;

    @JsonProperty("id")
    @Column(name = "uId")
    String uId;

    @JsonProperty("name")
    @Column(name = "name")
    String name;

    @JsonProperty("displayName")
    @Column(name = "displayName")
    String displayName;

    @JsonProperty("created")
    @Column(name = "created")
    String created;

    @JsonProperty("lastUpdated")
    @Column(name = "lastUpdated")
    String  lastUpdated;

    @JsonProperty("access")
    @Column(name = "access")
    Access access;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getUId() {
        return uId;
    }

    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getCreated() {
        return created;
    }

    public String getCreatededToPrint() {
        DateTime dateTime = DateTimeFormat.forPattern(TIMESTAMP_PATTERN_MILLIS).parseDateTime(
                created);
        return dateTime.toString(TIMESATAM_PATTERN_TO_PRINT);
    }

    @Override
    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public Access getAccess() {
        return access;
    }

    @Override
    public void setAccess(Access access) {
        this.access = access;
    }

    public String getLastUpdatedToPrint() {
        DateTime dateTime = DateTimeFormat.forPattern(TIMESTAMP_PATTERN_MILLIS).parseDateTime(
                lastUpdated);
        return dateTime.toString(TIMESATAM_PATTERN_TO_PRINT);
    }

    @Override
    public String toString() {
        return StringUtils.create()
                .append("BaseIdentifiableObject {")
                .append("id=").append(id)
                .append(", uId=").append(uId)
                .append(", name=").append(name)
                .append(", displayName=").append(displayName)
                .append(", created=").append(created == null ? "" : created.toString())
                .append(", lastUpdated=").append(lastUpdated == null ? "" : lastUpdated.toString())
                .append(", access=").append(access == null ? "" : access.toString())
                .append("}")
                .build();
    }

    public static <T extends BaseIdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
        Map<String, T> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                if (object.getUId() != null) {
                    map.put(object.getUId(), object);
                }
            }
        }
        return map;
    }

    public static <T extends BaseIdentifiableObject> List<String> toListIds(List<T> objects) {
        List<String> ids = new ArrayList<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                ids.add(object.getUId());
            }
        }
        return ids;
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
