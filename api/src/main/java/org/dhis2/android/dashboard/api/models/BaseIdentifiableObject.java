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

package org.dhis2.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.api.utils.StringUtils;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseIdentifiableObject extends BaseModel implements IdentifiableObject {

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
    DateTime created;

    @JsonProperty("lastUpdated")
    @Column(name = "lastUpdated")
    DateTime lastUpdated;

    @JsonProperty("access")
    @Column(name = "access")
    Access access;

    @JsonIgnore
    @Override
    public long getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public String getUId() {
        return uId;
    }

    @JsonIgnore
    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return name;
    }

    @JsonIgnore
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    @Override
    public DateTime getCreated() {
        return created;
    }

    @JsonIgnore
    @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @JsonIgnore
    @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public Access getAccess() {
        return access;
    }

    @JsonIgnore
    @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public void setAccess(Access access) {
        this.access = access;
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
}
