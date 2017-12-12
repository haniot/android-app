package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public Session(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("ocariot", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user data on shared preferences.
     *
     * @param _id String
     * @param token String
     * @return boolean
     */
    public boolean setLogged(String _id, String token) {
        editor.putString("userLogged", _id);
        editor.putString("userLoggedToken", token);

        return editor.commit();
    }

    /**
     * Get _id
     *
     * @return String
     */
    public String getIdLogged() {
        return prefs.getString("userLogged", null);
    }

    /**
     * Get token.
     * @return String
     */
    public String getTokenLogged() {
        return prefs.getString("userLoggedToken", "");
    }

    /**
     * User it is logged?
     *
     * @return boolean
     */
    public boolean isLogged() {
        return getIdLogged() != null;
    }

    /**
     * Remove user logged.
     *
     * @return boolean
     */
    public boolean removeLogged() {
        editor.remove("userLogged");
        editor.remove("userLoggedToken");

        return editor.commit();
    }

    /**
     * Save other string.
     *
     * @param key
     * @param value
     * @return boolean
     */
    public boolean putString(String key, String value) {
        editor.putString(key, value);

        return editor.commit();
    }

    public String getString(String key) {
        return prefs.getString(key, "");
    }

    /**
     * Remove other string.
     *
     * @return boolean
     */
    public boolean removeString(String key) {
        editor.remove(key);

        return editor.commit();
    }
}
