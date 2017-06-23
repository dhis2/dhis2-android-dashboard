package org.hisp.dhis.android.dashboard.api.models;

import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class EventReport extends BaseIdentifiableObject {

    UIDObject program;
    UIDObject programStage;
    List<UIDObject> organisationUnits;
    RelativePeriod relativePeriods;
    List<DataElementDimension> dataElementDimensions;
    UIDObject dataElementValueDimension;
    String aggregationType;
    String outputType;

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


}
