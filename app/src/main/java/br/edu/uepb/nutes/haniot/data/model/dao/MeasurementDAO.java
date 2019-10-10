package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MeasurementOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * MeasurementDAO implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementDAO {
    public static MeasurementDAO instance;
    private static Box<MeasurementOB> measurementBox;

    private MeasurementDAO() {
    }

    public static synchronized MeasurementDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        measurementBox = boxStore.boxFor(MeasurementOB.class);

        return instance;
    }

    /**
     * Adds a new measurementOB to the database.
     *
     * @param measurementOB
     * @return boolean
     */
    public boolean save(@NonNull MeasurementOB measurementOB) {
        return measurementBox.put(measurementOB) > 0;
    }

    /**
     * Update measurementOB data.
     *
     * @param measurementOB
     * @return boolean
     */
    public boolean update(@NonNull MeasurementOB measurementOB) {
        if (measurementOB.getId() == 0) {
            /**
             * Id is required for an updateOrSave
             * Otherwise it will be an insert
             */
            return false;
        }
        return save(measurementOB); // updateOrSave
    }

    /**
     * Remove measurementOB.
     *
     * @param measurementOB
     * @return boolean
     */
    public boolean remove(@NonNull MeasurementOB measurementOB) {
        return measurementBox.query()
                .equal(MeasurementOB_.id, measurementOB.getId())
                .build()
                .remove() > 0;
    }

    /**
     * Remove all measurements associated with device and user.
     *
     * @param deviceId long
     * @param userId   long
     * @return boolean
     */
    public boolean removeAll(@NonNull long deviceId, @NonNull long userId) {
        return measurementBox.query()
                .equal(MeasurementOB_.deviceId, deviceId)
                .equal(MeasurementOB_.userId, userId)
                .build()
                .remove() > 0;
    }

    /**
     * Remove all measurements associated with user.
     *
     * @param userId long
     * @return boolean
     */
    public boolean removeAll(@NonNull String userId) {
        return measurementBox.query()
                .equal(MeasurementOB_.userId, userId)
                .build()
                .remove() > 0;
    }

    /**
     * Select a measurement.
     *
     * @param id long
     * @return Object
     */
    public MeasurementOB get(@NonNull long id) {
        return measurementBox.query()
                .equal(MeasurementOB_.id, id)
                .build()
                .findFirst();
    }

    /**
     * Select all measurements associated with the user.
     *
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<MeasurementOB> list(@NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(MeasurementOB_.userId, userId)
                .orderDesc(MeasurementOB_.id)
                .build()
                .find(offset, limit);
    }

    /**
     * Select all measurements of a type associated with the user.
     *
     * @param type {@link String}
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<MeasurementOB> list(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(MeasurementOB_.type, type)
                .equal(MeasurementOB_.userId, userId)
                .orderDesc(MeasurementOB_.timestamp)
                .build()
                .find(offset, limit);
    }

    /**
     * Select all measurements associated with the device and user.
     *
     * @param deviceId long
     * @param userId   long
     * @param offset   int
     * @param limit    int
     * @return List<MeasurementOB>
     */
    public List<MeasurementOB> list(@NonNull long deviceId, @NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(MeasurementOB_.deviceId, deviceId)
                .equal(MeasurementOB_.userId, userId)
                .orderDesc(MeasurementOB_.id)
                .build()
                .find(offset, limit);
    }

    /**
     * Select all measurements of a user that were not sent to the remote server.
     *
     * @param userId long
     * @return List<MeasurementOB>
     */
    public List<MeasurementOB> getNotSent(@NonNull long userId) {
        return measurementBox.query()
                .equal(MeasurementOB_.userId, userId)
                .build()
                .find();
    }
}
