package br.edu.uepb.nutes.haniot.data.repository.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import br.edu.uepb.nutes.haniot.model.UserAccess;

/**
 * Class to perform operations on the device's shared preference.
 * The data is saved encrypted.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class AppPreferencesHelper implements PreferencesHelper {
    private final String PREF_KEY_AUTH_STATE_OCARIOT = "pref_key_user_access_ocariot";
    private final String PREF_KEY_AUTH_STATE_FITBIT = "pref_key_access_fitbit";
    private final String PREF_KEY_USER_PROFILE = "pref_key_user_profile";

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
    public boolean addUserAccessHaniot(UserAccess userAccess) {
        return false;
    }

    @Override
    public boolean addString(String key, String value) {
        return false;
    }

    @Override
    public boolean addBoolean(String key, boolean value) {
        return false;
    }

    @Override
    public boolean addInt(String key, int value) {
        return false;
    }

    @Override
    public boolean addLong(String key, long value) {
        return false;
    }

    @Override
    public UserAccess getUserAccessHaniot() {
        return null;
    }

    @Override
    public boolean removeUserAccessHaniot() {
        return false;
    }

    @Override
    public boolean removeItem(String key) {
        return false;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }
}
