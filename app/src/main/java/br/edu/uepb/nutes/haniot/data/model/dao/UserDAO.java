package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.User;
import br.edu.uepb.nutes.haniot.data.model.objectbox.User_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents UserDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
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
     * get user for _id.
     *
     * @param _id String
     * @return User
     */
    public User get(@NonNull String _id) {
        return userBox.query().equal(User_._id, _id).build().findFirst();
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
        User userUp = get(user.get_id());
        /**
         * Id is required for an update
         * Otherwise it will be an insert
         */
        if (userUp == null) return false;

        user.setId(userUp.getId());
        if (user.get_id() == null) user.set_id(userUp.get_id());

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


    /**
     * Remove user.
     *
     * @param id
     * @return boolean
     */
    public boolean remove(@NonNull String id) {
        return userBox.query().equal(User_._id, id).build().remove() > 0;
    }
}
