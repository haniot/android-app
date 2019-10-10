package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.UserOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.UserOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents UserDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class UserDAO {

    public static UserDAO instance;
    private static Box<UserOB> userBox;

    private UserDAO() {
    }

    public static synchronized UserDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new UserDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        userBox = boxStore.boxFor(UserOB.class);

        return instance;
    }

    /**
     * get user for _id.
     *
     * @param _id String
     * @return UserOB
     */
    public UserOB get(@NonNull String _id) {
        return userBox.query().equal(UserOB_._id, _id).build().findFirst();
    }

    /**
     * Selects user based on local id
     *
     * @param id long
     * @return UserOB
     */
    public UserOB get(@NonNull long id) {
        return userBox.query().equal(UserOB_.id, id).build().findFirst();
    }

    public List<UserOB> listAll() {
        return null;
    }

    /**
     * Add new user.
     *
     * @param user
     * @return boolean
     */
    public boolean save(@NonNull UserOB user) {
        return userBox.put(user) > 0;
    }

    /**
     * Update user.
     *
     * @param user
     * @return boolean
     */
    public boolean update(@NonNull UserOB user) {
        UserOB userUp = get(user.get_id());
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
        return userBox.query().equal(UserOB_.id, id).build().remove() > 0;
    }


    /**
     * Remove user.
     *
     * @param id
     * @return boolean
     */
    public boolean remove(@NonNull String id) {
        return userBox.query().equal(UserOB_._id, id).build().remove() > 0;
    }
}
