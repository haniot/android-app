package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly_;
import br.edu.uepb.nutes.haniot.model.elderly.Item;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.relation.ToMany;

/**
 * ElderlyDAO implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class ElderlyDAO {
    private static ElderlyDAO instance;
    private static Box<Elderly> elderlyBox;
    private static Box<Item> itemBox;

    private ElderlyDAO() {
    }

    /**
     * @param context
     * @return ElderlyDAO
     */
    public static synchronized ElderlyDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new ElderlyDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        elderlyBox = boxStore.boxFor(Elderly.class);
        itemBox = boxStore.boxFor(Item.class);

        return instance;
    }

    /**
     * Select a Elderly.
     *
     * @param id long
     * @return {@link Elderly}
     */
    public Elderly get(@NonNull long id) {
        return elderlyBox.query()
                .equal(Elderly_.id, id)
                .build()
                .findFirst();
    }

    /**
     * Select a Elderly.
     *
     * @param _id String
     * @return {@link Elderly}
     */
    public Elderly get(@NonNull String _id) {
        return elderlyBox.query()
                .equal(Elderly_._id, _id)
                .build()
                .findFirst();
    }

    /**
     * Select all elderlies associated with the user.
     *
     * @param userId long
     * @return {@link List<Elderly>}
     */
    public List<Elderly> list(@NonNull long userId) {
        return elderlyBox.query()
                .equal(Elderly_.userId, userId)
                .orderDesc(Elderly_.id)
                .build()
                .find();
    }

    /**
     * Adds a new elderly to the database.
     *
     * @param elderly {@link Elderly}
     * @return {@link Elderly}
     */
    public Elderly save(@NonNull Elderly elderly) {
        long id = elderlyBox.put(elderly);
        return get(id);
    }

    /**
     * Update elderly.
     *
     * @param elderly {@link Elderly}
     * @return {@link Elderly}
     */
    public Elderly update(@NonNull Elderly elderly) {
        if (elderly.getId() == 0) {
            Elderly elderlyUp = get(elderly.get_id());

            /**
             * Id is required for an update
             * Otherwise it will be an insert
             */
            if (elderlyUp == null) return null;

            elderly.setId(elderlyUp.getId());
        }

        return save(elderly); // update
    }

    /**
     * Remove elderly.
     *
     * @param id
     * @return boolean
     */
    public boolean remove(@NonNull long id) {
        return elderlyBox.query()
                .equal(Elderly_.id, id)
                .build()
                .remove() > 0 &&
                itemBox.query().build().remove() > 0;
    }

    /**
     * Remove elderly.
     *
     * @param elderly {@link Elderly}
     * @return boolean
     */
    public boolean remove(@NonNull Elderly elderly) {
        return remove(elderly.getId());
    }

    /**
     * Remove all elderlies associated with user.
     *
     * @param userId long
     * @return boolean
     */
    public boolean removeAll(@NonNull long userId) {
        return elderlyBox.query()
                .equal(Elderly_.userId, userId)
                .build()
                .remove() > 0 &&
                itemBox.query().build().remove() > 0;
    }

    /**
     * Recover medications used by the elderly.
     *
     * @param elderlyId long
     * @return {@link List<Medication>}
     */
    public List<Item> listMedications(@NonNull long elderlyId) {
        ToMany<Item> medications = elderlyBox.query()
                .equal(Elderly_.id, elderlyId)
                .build()
                .findFirst().getMedications();
        return new ArrayList<>(medications);
    }

    /**
     * Recover accessories used by the elderly.
     *
     * @param elderlyId long
     * @return {@link List<Accessory>}
     */
    public List<Item> listAccessories(@NonNull long elderlyId) {
        ToMany<Item> accessories = elderlyBox.query()
                .equal(Elderly_.id, elderlyId)
                .build()
                .findFirst().getAccessories();
        return new ArrayList<>(accessories);
    }
}
