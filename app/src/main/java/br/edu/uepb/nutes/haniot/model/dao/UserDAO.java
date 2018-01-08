package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.User_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents UserDAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.6
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class UserDAO {

    public static UserDAO instance;
    private static Box<User> userBox;

    private UserDAO() {
    }

    public static synchronized UserDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new UserDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        userBox = boxStore.boxFor(User.class);

        return instance;
    }

    /**
     * get user server remote.
     *
     * @param uuid String
     * @return User
     */
    public User get(@NonNull String uuid) {
        return userBox.query().equal(User_._id, uuid).build().findFirst();
    }

    /**
     * Selects user based on local id
     *
     * @param id long
     * @return User
     */
    public User get(@NonNull long id) {
        return userBox.query().equal(User_.id, id).build().findFirst();
    }

    public List<User> listAll() {
        return null;
    }

    /**
     * Add new user.
     *
     * @param user
     * @return boolean
     */
    public boolean save(@NonNull User user) {
        return userBox.put(user) > 0;
    }

    /**
     * Update user.
     *
     * @param user
     * @return boolean
     */
    public boolean update(@NonNull User user) {
        if (user.getId() == 0) {
            User userUp = get(user.get_id());

            /**
             * Id is required for an update
             * Otherwise it will be an insert
             */
            if (userUp == null) return false;

            user.setId(userUp.getId());
        }

        return save(user); // update
    }

    /**
     * Remove user.
     *
     * @param id
     * @return boolean
     */
    public boolean remove(@NonNull long id) {
        return userBox.query().equal(User_.id, id).build().remove() > 0;
    }
}
