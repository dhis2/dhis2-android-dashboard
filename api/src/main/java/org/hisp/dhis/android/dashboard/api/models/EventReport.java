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
        for (UIDObject filter : filters) {
            if (filter.getuId().equals("ou")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPEInFilters() {
        for (UIDObject filter : filters) {
            if (filter.getuId().equals("pe")) {
                return true;
            }
        }
        return false;
    }

    public boolean isDimensionInFilters(String dimension) {
        for (UIDObject filter : filters) {
            if (filter.getuId().equals(dimension)) {
                return true;
            }
        }
        return false;
    }

    public String getDataTypeString() {
        if (dataType.equals(AGGREGATED_VALUES_TYPE)) {
            return "aggregate";
        } else {
            return "query";
        }
    }

    public boolean isValidColumn(UIDObject column) {
        if (column.getuId().equals("pe") || column.getuId().equals("ou")) {
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
