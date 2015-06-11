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

package org.dhis2.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

public final class UserAccount {
    /**
     * Value of 'fields' query parameter for getCurrentUserAccount() method
     */
    public static final String ALL_USER_ACCOUNT_FIELDS = "[id,created,lastUpdated,name,displayName," +
            "firstName,surname," +
            "gender,birthday,introduction," +
            "education,employer,interests," +
            "jobTitle,languages,email,phoneNumber," +
            "organisationUnits[id]]";

    @JsonProperty("id") private String id;
    @JsonProperty("created") private DateTime created;
    @JsonProperty("lastUpdated") private DateTime lastUpdated;
    @JsonProperty("name") private String name;
    @JsonProperty("displayName") private String displayName;
    @JsonProperty("firstName") private String firstName;
    @JsonProperty("surname") private String surname;
    @JsonProperty("gender") private String gender;
    @JsonProperty("birthday") private String birthday;
    @JsonProperty("introduction") private String introduction;
    @JsonProperty("education") private String education;
    @JsonProperty("employer") private String employer;
    @JsonProperty("interests") private String interests;
    @JsonProperty("jobTitle") private String jobTitle;
    @JsonProperty("languages") private String languages;
    @JsonProperty("email") private String email;
    @JsonProperty("phoneNumber") private String phoneNumber;

    public UserAccount() {
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public DateTime getCreated() {
        return created;
    }

    @JsonIgnore
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @JsonIgnore
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonIgnore
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public String getFirstName() {
        return firstName;
    }

    @JsonIgnore
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonIgnore
    public String getSurname() {
        return surname;
    }

    @JsonIgnore
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonIgnore
    public String getGender() {
        return gender;
    }

    @JsonIgnore
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonIgnore
    public String getBirthday() {
        return birthday;
    }

    @JsonIgnore
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @JsonIgnore
    public String getIntroduction() {
        return introduction;
    }

    @JsonIgnore
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @JsonIgnore
    public String getEducation() {
        return education;
    }

    @JsonIgnore
    public void setEducation(String education) {
        this.education = education;
    }

    @JsonIgnore
    public String getEmployer() {
        return employer;
    }

    @JsonIgnore
    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @JsonIgnore
    public String getInterests() {
        return interests;
    }

    @JsonIgnore
    public void setInterests(String interests) {
        this.interests = interests;
    }

    @JsonIgnore
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonIgnore
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonIgnore
    public String getLanguages() {
        return languages;
    }

    @JsonIgnore
    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonIgnore
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}