/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.dhis2.android.dashboard.api.network.repository;

import org.dhis2.mobile.sdk.persistence.models.Category;
import org.dhis2.mobile.sdk.persistence.models.CategoryCombo;
import org.dhis2.mobile.sdk.persistence.models.CategoryOption;
import org.dhis2.mobile.sdk.persistence.models.DataElement;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.models.SystemInfo;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface DhisService {

    @GET("/system/info/") SystemInfo getSystemInfo();

    @GET("/me/") UserAccount getCurrentUserAccount(@QueryMap Map<String, String> queryParams);

    @GET("/organisationUnits?paging=false") Map<String, List<OrganisationUnit>> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets?paging=false") Map<String, List<DataSet>> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("/dataElements?paging=false") Map<String, List<DataElement>> getDataElements(@QueryMap Map<String, String> queryParams);

    @GET("/categoryCombos?paging=false") Map<String, List<CategoryCombo>> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("/categories?paging=false") Map<String, List<Category>> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("/categoryOptions?paging=false") Map<String, List<CategoryOption>> getCategoryOptions(@QueryMap Map<String, String> queryMap);

}