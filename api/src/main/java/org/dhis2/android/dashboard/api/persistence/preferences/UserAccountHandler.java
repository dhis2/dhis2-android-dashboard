/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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

package org.dhis2.android.dashboard.api.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.dhis2.android.dashboard.api.persistence.DateTimeConverter;
import org.dhis2.android.dashboard.api.models.UserAccount;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class UserAccountHandler {
    private static final String USER_ACCOUNT_PREFERENCES = "userAccountPreferences";

    private static final String ID = "id";
    private static final String CREATED = "created";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "displayName";
    private static final String FIRST_NAME = "firstName";
    private static final String SURNAME = "surname";
    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";
    private static final String INTRODUCTION = "introduction";
    private static final String EDUCATION = "education";
    private static final String EMPLOYER = "employer";
    private static final String INTERESTS = "interests";
    private static final String JOB_TITLE = "jobTitle";
    private static final String LANGUAGES = "languages";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phoneNumber";

    private final SharedPreferences mPrefs;

    public UserAccountHandler(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(USER_ACCOUNT_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public void put(UserAccount account) {
        isNull(account, "UserAccount must not be null");

        DateTimeConverter converter = new DateTimeConverter();

        put(ID, account.getId());
        put(CREATED, converter.getDBValue(account.getCreated()));
        put(LAST_UPDATED, converter.getDBValue(account.getLastUpdated()));
        put(NAME, account.getName());
        put(DISPLAY_NAME, account.getDisplayName());
        put(FIRST_NAME, account.getFirstName());
        put(SURNAME, account.getSurname());
        put(GENDER, account.getGender());
        put(BIRTHDAY, account.getBirthday());
        put(INTRODUCTION, account.getIntroduction());
        put(EDUCATION, account.getEducation());
        put(EMPLOYER, account.getEmployer());
        put(INTERESTS, account.getInterests());
        put(JOB_TITLE, account.getJobTitle());
        put(LANGUAGES, account.getLanguages());
        put(EMAIL, account.getEmail());
        put(PHONE_NUMBER, account.getPhoneNumber());
    }

    public void delete() {
        mPrefs.edit().clear().apply();
    }

    public UserAccount get() {
        DateTimeConverter converter = new DateTimeConverter();

        UserAccount userAccount = new UserAccount();
        userAccount.setId(get(ID));
        userAccount.setCreated(converter.getModelValue(CREATED));
        userAccount.setLastUpdated(converter.getModelValue(LAST_UPDATED));
        userAccount.setName(get((NAME)));
        userAccount.setDisplayName(get(DISPLAY_NAME));
        userAccount.setFirstName(get(FIRST_NAME));
        userAccount.setSurname(get(SURNAME));
        userAccount.setGender(get(GENDER));
        userAccount.setBirthday(get(BIRTHDAY));
        userAccount.setIntroduction(get(INTRODUCTION));
        userAccount.setEducation(get(EDUCATION));
        userAccount.setEmployer(get(EMPLOYER));
        userAccount.setInterests(get(INTERESTS));
        userAccount.setJobTitle(get(JOB_TITLE));
        userAccount.setLanguages(get(LANGUAGES));
        userAccount.setEmail(get(EMAIL));
        userAccount.setPhoneNumber(get(PHONE_NUMBER));
        return userAccount;
    }

    private void put(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String get(String key) {
        return mPrefs.getString(key, null);
    }
}
