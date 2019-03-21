package br.edu.uepb.nutes.haniot.data.repository.local.pref;

import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.UserAccess;

/**
 * Interface for Preferences Helper.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface PreferencesHelper {
    boolean saveUserAccessHaniot(final UserAccess userAccess);

    boolean saveUserLogged(final User user);

    boolean saveString(String key, String value);

    boolean saveBoolean(String key, boolean value);

    boolean saveInt(String key, int value);

    boolean saveLong(String key, long value);

    boolean saveFloat(String key, float value);

    UserAccess getUserAccessHaniot();

    User getUserLogged();

    String getString(String key);

    boolean getBoolean(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    boolean removeUserAccessHaniot();

    boolean removeUserLogged();

    boolean removeItem(String key);
}
