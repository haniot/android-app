package br.edu.uepb.nutes.haniot.data.repository.local.pref;

import br.edu.uepb.nutes.haniot.data.model.objectbox.Patient;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.objectbox.User;
import br.edu.uepb.nutes.haniot.data.model.objectbox.UserAccess;

/**
 * Interface for Preferences Helper.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface PreferencesHelper {
    boolean saveUserAccessHaniot(final UserAccess userAccess);

    boolean saveUserLogged(final User user);

    boolean saveLastPatient(Patient patient);

    boolean saveLastPilotStudy(PilotStudy pilot);

    boolean saveString(String key, String value);

    boolean saveBoolean(String key, boolean value);

    boolean saveInt(String key, int value);

    boolean saveLong(String key, long value);

    boolean saveFloat(String key, float value);

    boolean saveBluetoothMode(boolean activated);

    UserAccess getUserAccessHaniot();

    User getUserLogged();

    Patient getLastPatient();

    PilotStudy getLastPilotStudy();

    boolean getBluetoothMode();

    String getString(String key);

    boolean getBoolean(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    boolean removeUserAccessHaniot();

    boolean removeUserLogged();

    boolean removeLastPilotStudy();

    boolean removeLastPatient();

    boolean removeItem(String key);
}
