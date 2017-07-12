package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class EventReport extends BaseIdentifiableObject {
    @JsonIgnore
    static final String AGGREGATED_VALUES_TYPE = "AGGREGATED_VALUES";
    @JsonIgnore
    static final String EVENTS_TYPE = "EVENTS";
    @JsonIgnore
    static final String OU_KEY = "ou";
    @JsonIgnore
    static final String PE_KEY = "pe";
    @JsonIgnore
    static final String AGGREGATE_KEY = "aggregate";
    @JsonIgnore
    static final String QUERY_KEY = "query";


    UIDObject program;
    UIDObject programStage;
    List<UIDObject> organisationUnits;
    RelativePeriod relativePeriods;
    List<DataElementDimension> dataElementDimensions;
    UIDObject dataElementValueDimension;
    String aggregationType;
    String outputType;
    String dataType;
    List<AttributeDimension> attributeDimensions;
    List<UIDObject> filters;
    List<UIDObject> columns;

    public UIDObject getProgram() {
        return program;
    }

    public void setProgram(UIDObject program) {
        this.program = program;
    }

    public UIDObject getProgramStage() {
        return programStage;
    }

    public void setProgramStage(UIDObject programStage) {
        this.programStage = programStage;
    }

    public List<UIDObject> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(
            List<UIDObject> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    public RelativePeriod getRelativePeriods() {
        return relativePeriods;
    }

    public void setRelativePeriods(RelativePeriod relativePeriods) {
        this.relativePeriods = relativePeriods;
    }

    public List<DataElementDimension> getDataElementDimensions() {
        return dataElementDimensions;
    }

    public void setDataElementDimensions(
            List<DataElementDimension> dataElementDimensions) {
        this.dataElementDimensions = dataElementDimensions;
    }

    public UIDObject getDataElementValueDimension() {
        return dataElementValueDimension;
    }

    public void setDataElementValueDimension(
            UIDObject dataElementValueDimension) {
        this.dataElementValueDimension = dataElementValueDimension;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<AttributeDimension> getAttributeDimensions() {
        return attributeDimensions;
    }

    public void setAttributeDimensions(
            List<AttributeDimension> attributeDimensions) {
        this.attributeDimensions = attributeDimensions;
    }

    public List<UIDObject> getFilters() {
        return filters;
    }

    public void setFilters(List<UIDObject> filters) {
        this.filters = filters;
    }

    public List<UIDObject> getColumns() {
        return columns;
    }

    public void setColumns(List<UIDObject> columns) {
        this.columns = columns;
    }

    public String getOUDimensionFilter() {
        String ouDimensions = "ou:";
        boolean firstOu = true;
        for (UIDObject organizationUnit : organisationUnits) {
            ouDimensions +=
                    firstOu ? organizationUnit.getuId() : ";" + organizationUnit.getuId();
            firstOu = false;
        }
        return ouDimensions;
    }

    public String getDimensionFilter(DataElementDimension dimension) {
        String dimensionUID = "";
        dimensionUID += dimension.getDataElement().getuId();
        if (dimension.getLegendSet() != null) {
            dimensionUID += "-" + dimension.getLegendSet().getuId();
        }
        if (dimension.getFilter() != null && !dimension.getFilter().isEmpty()) {
            dimensionUID += ":" + dimension.getFilter();
        }
        return dimensionUID;
    }

    public boolean isOUInFilters() {
        return isInFilters(OU_KEY);
    }

    public boolean isPEInFilters() {
        return isInFilters(PE_KEY);
    }

    public boolean isInFilters(String key){
        for (UIDObject filter : filters) {
            if (filter.getuId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public String getDataTypeString() {
        return (dataType.equals(AGGREGATED_VALUES_TYPE)) ? AGGREGATE_KEY : QUERY_KEY;
    }

    public boolean isValidColumn(UIDObject column) {
        if (column.getuId().equals(PE_KEY) || column.getuId().equals(OU_KEY)) {
            return false;
        }
        for (DataElementDimension dimension : dataElementDimensions) {
            if (dimension.getDataElement().getuId().equals(column.getuId())) {
                return false;
            }
        }
        return true;
    }
}
