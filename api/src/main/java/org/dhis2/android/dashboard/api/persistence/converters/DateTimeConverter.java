package org.dhis2.android.dashboard.api.persistence.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

/**
 * This class is used to automatically convert DateTime object to String
 * and backwards during read/write operations to database
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public final class DateTimeConverter extends TypeConverter<String, DateTime> {

    @Override public String getDBValue(DateTime model) {
        return model.toString();
    }

    @Override public DateTime getModelValue(String data) {
        return DateTime.parse(data);
    }
}
