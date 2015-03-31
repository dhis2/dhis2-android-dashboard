package org.hisp.dhis.mobile.datacapture.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class UserAccountPreferences {
    private static final String USER_PREFERENCES = "preferences:UserAccount";

    private static final String BIRTHDAY = "birthday";
    private static final String EDUCATION = "education";
    private static final String EMAIL = "email";
    private static final String EMPLOYER = "employer";
    private static final String FIRST_NAME = "firstName";
    private static final String GENDER = "gender";
    private static final String INTERESTS = "interests";
    private static final String INTRODUCTION = "introduction";
    private static final String JOB_TITLE = "jobTitle";
    private static final String LANGUAGES = "languages";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String SURNAME = "surname";
    private static final String USERNAME = "username";

    private static final String STATE = "state";

    private SharedPreferences mPrefs;

    public UserAccountPreferences(Context context) {
        isNull(context, "Context object must not be null");

        mPrefs = context.getSharedPreferences(USER_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public void put(UserAccount account) {
        isNull(account, "UserAccount object must not be null");

        put(BIRTHDAY, account.getBirthday());
        put(EDUCATION, account.getEducation());
        put(EMAIL, account.getEmail());
        put(EMPLOYER, account.getEmployer());
        put(FIRST_NAME, account.getFirstName());
        put(GENDER, account.getGender());
        put(INTERESTS, account.getInterests());
        put(INTRODUCTION, account.getIntroduction());
        put(JOB_TITLE, account.getJobTitle());
        put(LANGUAGES, account.getLanguages());
        put(PHONE_NUMBER, account.getPhoneNumber());
        put(SURNAME, account.getSurname());
        put(USERNAME, account.getUsername());
    }

    public UserAccount get() {
        UserAccount userAccount = new UserAccount();

        userAccount.setBirthday(get(BIRTHDAY));
        userAccount.setEducation(get(EDUCATION));
        userAccount.setEmail(get(EMAIL));
        userAccount.setEmployer(get(EMPLOYER));
        userAccount.setFirstName(get(FIRST_NAME));
        userAccount.setGender(get(GENDER));
        userAccount.setInterests(get(INTERESTS));
        userAccount.setIntroduction(get(INTRODUCTION));
        userAccount.setJobTitle(get(JOB_TITLE));
        userAccount.setLanguages(get(LANGUAGES));
        userAccount.setPhoneNumber(get(PHONE_NUMBER));
        userAccount.setSurname(get(SURNAME));
        userAccount.setUsername(get(USERNAME));

        return userAccount;
    }

    public void putState(State state) {
        put(STATE, state.toString());
    }

    public State getState() {
        String stateString = get(STATE);
        State state = null;
        if (stateString != null) {
            state = State.valueOf(stateString);
        }
        return state;
    }

    private void put(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String get(String key) {
        return mPrefs.getString(key, null);
    }

    public void delete() {
        mPrefs.edit().clear().apply();
    }
}
