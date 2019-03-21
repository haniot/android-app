package br.edu.uepb.nutes.haniot.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.exception.LocalPreferenceException;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;

/**
 * Class to perform operations on the device's shared preference.
 * The data is saved encrypted.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_AUTH_STATE_HANIOT = "pref_key_user_access_haniot";
    private final String PREF_KEY_USER_PROFILE = "pref_key_user_profile";
    private final String PREF_KEY_PATIENT = "pref_key_patient";

    private static AppPreferencesHelper instance;
    private SharedPreferences mPrefs;

    private AppPreferencesHelper(Context context) {
        mPrefs = new SecurePreferences(context);
    }

    public static synchronized AppPreferencesHelper getInstance(Context context) {
        if (instance == null) instance = new AppPreferencesHelper(context);
        return instance;
    }

    @Override
    public boolean saveUserAccessHaniot(UserAccess userAccess) {
        if (userAccess.getAccessToken() == null || userAccess.getAccessToken().isEmpty())
            throw new LocalPreferenceException("attribute accessToken can not be null or empty!");

        return mPrefs.edit()
                .putString(PREF_KEY_AUTH_STATE_HANIOT, String.valueOf(userAccess.toJson()))
                .commit();
    }

    @Override
    public boolean saveUserLogged(User user) {
        if (user == null || user.get_id() == null) {
            throw new LocalPreferenceException("attribute user can not be null or empty!");
        }

        return mPrefs.edit()
                .putString(PREF_KEY_USER_PROFILE, String.valueOf(user.toJson()))
                .commit();
    }

    @Override
    public boolean saveLastPatient(Patient patient) {
        if (patient == null) {
            throw new LocalPreferenceException("attribute patient can not be null or empty!");
        }

        return mPrefs.edit()
                .putString(PREF_KEY_PATIENT, String.valueOf(patient.toJson()))
                .commit();
    }

    @Override
    public boolean saveString(String key, String value) {
        checkKey(key);
        return mPrefs.edit().putString(key, value).commit();
    }

    @Override
    public boolean saveBoolean(String key, boolean value) {
        checkKey(key);
        return mPrefs.edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean saveInt(String key, int value) {
        checkKey(key);
        return mPrefs.edit().putInt(key, value).commit();
    }

    @Override
    public boolean saveLong(String key, long value) {
        checkKey(key);
        return mPrefs.edit().putLong(key, value).commit();
    }

    @Override
    public boolean saveFloat(String key, float value) {
        checkKey(key);
        return mPrefs.edit().putFloat(key, value).commit();
    }

    @Override
    public UserAccess getUserAccessHaniot() {
        String userAccess = mPrefs.getString(PREF_KEY_AUTH_STATE_HANIOT, null);
        return UserAccess.jsonDeserialize(userAccess);
    }

    @Override
    public User getUserLogged() {
        String user = mPrefs.getString(PREF_KEY_USER_PROFILE, null);
        return User.jsonDeserialize(user);
    }

    @Override
    public Patient getlastPatient() {
        String patient = mPrefs.getString(PREF_KEY_PATIENT, null);
        return Patient.jsonDeserialize(patient);
    }

    @Override
    public String getString(String key) {
        checkKey(key);
        return mPrefs.getString(key, null);
    }

    @Override
    public boolean getBoolean(String key) {
        checkKey(key);
        return mPrefs.getBoolean(key, false);
    }

    @Override
    public int getInt(String key) {
        checkKey(key);
        return mPrefs.getInt(key, -1);
    }

    @Override
    public long getLong(String key) {
        checkKey(key);
        return mPrefs.getLong(key, -1);
    }

    @Override
    public float getFloat(String key) {
        checkKey(key);
        return mPrefs.getFloat(key, -1);
    }

    @Override
    public boolean removeUserAccessHaniot() {
        return mPrefs.edit().remove(PREF_KEY_AUTH_STATE_HANIOT).commit() && removeUserLogged();
    }

    @Override
    public boolean removeUserLogged() {
        return mPrefs.edit().remove(PREF_KEY_USER_PROFILE).commit() && removeUserAccessHaniot();
    }

    @Override
    public boolean removeItem(String key) {
        checkKey(key);
        return mPrefs.edit().remove(key).commit();
    }

    private void checkKey(String key) {
        if (key == null || key.isEmpty())
            throw new NullPointerException("key can not be null or empty!");
    }
}
