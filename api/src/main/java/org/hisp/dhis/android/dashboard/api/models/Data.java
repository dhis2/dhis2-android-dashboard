package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.dashboard.api.utils.StringUtils;

public final class Data {


    @JsonProperty("write")
    boolean write;

    @JsonProperty("read")
    boolean read;


    @JsonIgnore
    public boolean isRead() {
        return read;
    }

    @JsonIgnore
    public void setRead(boolean read) {
        this.read = read;
    }

    @JsonIgnore
    public boolean isWrite() {
        return write;
    }

    @JsonIgnore
    public void setWrite(boolean write) {
        this.write = write;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return StringUtils.create()
                .append("Data {")
                .append(", write=").append(write)
                .append(", read=").append(read)
                .append("}")
                .build();
    }
}
