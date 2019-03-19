package br.edu.uepb.nutes.haniot.data.repository.local.pref;

import br.edu.uepb.nutes.haniot.model.UserAccess;

/**
 * Interface for Preferences Helper.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface PreferencesHelper {
    boolean addUserAccessHaniot(final UserAccess userAccess);

    boolean addString(String key, String value);

    boolean addBoolean(String key, boolean value);

    boolean addInt(String key, int value);

    boolean addLong(String key, long value);

    UserAccess getUserAccessHaniot();

    boolean removeUserAccessHaniot();

    boolean removeItem(String key);

    String getString(String key);

    boolean getBoolean(String key);
}
