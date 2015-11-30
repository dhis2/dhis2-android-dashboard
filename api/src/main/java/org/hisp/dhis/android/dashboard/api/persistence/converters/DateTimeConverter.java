package org.hisp.dhis.android.dashboard.api.persistence.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

/**
 * This class is used to automatically convert DateTime object to String
 * and backwards during read/write operations to database
 */
@SuppressWarnings("unused")
@com.raizlabs.android.dbflow.annotation.TypeConverter
public final class DateTimeConverter extends TypeConverter<String, DateTime> {

    @Override
    public String getDBValue(DateTime model) {
        if (model != null) {
            return model.toString();
        }

        return null;
    }

    @Override
    public DateTime getModelValue(String data) {
        if (data != null) {
            return DateTime.parse(data);
        }

        return null;
    }
}
