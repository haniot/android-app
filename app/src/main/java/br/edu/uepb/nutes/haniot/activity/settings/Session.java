package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.Context;
import android.content.SharedPreferences;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.UserDAO;

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
        prefs = context.getSharedPreferences("haniot", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user data on shared preferences.
     *
     * @param id    long
     * @param token String
     * @return boolean
     */
    public boolean setLogged(long id, String token) {
        editor.putLong("userLoggedId", id);
        editor.putString("userLoggedToken", token);

        return editor.commit();
    }

    /**
     * Get id local
     *
     * @return long
     */
    public long getIdLogged() {
        return prefs.getLong("userLoggedId", -1);
    }

    /**
     * Get _id in remote server.
     *
     * @return String
     */
    public String get_idLogged() {
        return getUserLogged().get_id();
    }

    /**
     * Retrieve the logged user
     *
     * @return User
     */
    public User getUserLogged() {
        return UserDAO.getInstance(context).get(getIdLogged());
    }

    /**
     * Get token.
     *
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
        return getIdLogged() > 0;
    }

    /**
     * Remove user logged.
     *
     * @return boolean
     */
    public boolean removeLogged() {
        editor.remove("userLoggedId");
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
