package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.Measurement_;
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
    public boolean removeAll(@NonNull long userId) {
        return measurementBox.query()
                .equal(Measurement_.userId, userId)
                .build()
                .remove() > 0;
    }

    /**
     * Remove all measurements associated with the user that are expired.
     * A Measurement expires if a registration data for less than the current year
     *
     * @param userId long
     * @return boolean
     */
    public boolean removeAllExpired(@NonNull long userId) {
        return measurementBox.query()
                .equal(Measurement_.userId, userId)
                .less(Measurement_.registrationDate, DateUtils.getCurrentYear())
                .equal(Measurement_.hasSent, 0)
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
     * @param typeId int
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<Measurement>
     */
    public List<Measurement> list(@NonNull int typeId, @NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return measurementBox.query()
                .equal(Measurement_.typeId, typeId)
                .equal(Measurement_.userId, userId)
                .orderDesc(Measurement_.registrationDate)
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
     * Select measurements of a type associated with a user according to the start and end date.
     *
     * @param dateStart long
     * @param dateEnd   long
     * @param typeId    int
     * @param userId    long
     * @return List<Measurement>
     */
    public List<Measurement> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull int typeId, @NonNull long userId) {
        return measurementBox.query()
                .equal(Measurement_.typeId, typeId)
                .equal(Measurement_.userId, userId)
                .between(Measurement_.registrationDate, dateStart, dateEnd)
                .orderDesc(Measurement_.id)
                .build()
                .find();
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
                .equal(Measurement_.hasSent, 0)
                .build()
                .find();
    }

    /**
     * Select all measurements from a user that have been sent to the remote server.
     *
     * @param userId long
     * @return List<Measurement>
     */
    public List<Measurement> getWasSent(@NonNull long userId) {
        return measurementBox.query()
                .equal(Measurement_.userId, userId)
                .equal(Measurement_.hasSent, 1)
                .build()
                .find();
    }
}
