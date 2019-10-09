package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Measurement;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Measurement_;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
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
    private static Box<Measurement> measurementBox;

    private MeasurementDAO() {
    }

    public static synchronized MeasurementDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        measurementBox = boxStore.boxFor(Measurement.class);

        return instance;
    }

    /**
     * Adds a new measurement to the database.
     *
     * @param measurement
     * @return boolean
     */
    public boolean save(@NonNull Measurement measurement) {
        return measurementBox.put(measurement) > 0;
    }

    /**
     * Update measurement data.
     *
     * @param measurement
     * @return boolean
     */
    public boolean update(@NonNull Measurement measurement) {
        if (measurement.getId() == 0) {
            /**
             * Id is required for an updateOrSave
             * Otherwise it will be an insert
             */
            return false;
        }

        return save(measurement); // updateOrSave
    }

    /**
     * Remove measurement.
     *
     * @param measurement
     * @return boolean
     */
    public boolean remove(@NonNull Measurement measurement) {
        return measurementBox.query()
                .equal(Measurement_.id, measurement.getId())
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
                .equal(Measurement_.deviceId, deviceId)
                .equal(Measurement_.userId, userId)
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
                .equal(Measurement_.userId, userId)
                .build()
                .remove() > 0;
    }

    /**
     * Select a measurement.
     *
     * @param id long
     * @return Object
     */
    public Measurement get(@NonNull long id) {
        return measurementBox.query()
                .equal(Measurement_.id, id)
                .build()
                .findFirst();
    }

    /**
     * Select all measurements associated with the user.
     *
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<Measurement>
     */
    public List<Measurement> list(@NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(Measurement_.userId, userId)
                .orderDesc(Measurement_.id)
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
     * @return List<Measurement>
     */
    public List<Measurement> list(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(Measurement_.type, type)
                .equal(Measurement_.userId, userId)
                .orderDesc(Measurement_.timestamp)
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
     * @return List<Measurement>
     */
    public List<Measurement> list(@NonNull long deviceId, @NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(Measurement_.deviceId, deviceId)
                .equal(Measurement_.userId, userId)
                .orderDesc(Measurement_.id)
                .build()
                .find(offset, limit);
    }

    /**
     * Select all measurements of a user that were not sent to the remote server.
     *
     * @param userId long
     * @return List<Measurement>
     */
    public List<Measurement> getNotSent(@NonNull long userId) {
        return measurementBox.query()
                .equal(Measurement_.userId, userId)
                .build()
                .find();
    }
}
