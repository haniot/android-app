package br.edu.uepb.nutes.haniot.model.dao;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * MeasurementDAO interface.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface MeasurementDAO<T> {

    /**
     * Adds a new measurement to the database.
     *
     * @param o
     * @return boolean
     */
    public boolean save(@NonNull T o);

    /**
     * Updates object data.
     *
     * @param o
     * @return boolean
     */
    public boolean update(@NonNull T o);

    /**
     * Removes measurement passed as parameter.
     *
     * @param o
     * @return boolean
     */
    public boolean remove(@NonNull T o);

    /**
     * Removes all measurements of a device.
     *
     * @param deviceAddress String
     * @param userId        String
     * @return long - Total number of items removed
     */
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId);

    /**
     * Select a Measurement.
     *
     * @param id long
     * @return Object
     */
    public T get(@NonNull long id);

    /**
     * Retrieves all measurements from device and user.
     *
     * @param deviceAddress String
     * @param userId        String
     * @return List<T>
     */
    public List<T> listAll(@NonNull String deviceAddress, @NonNull String userId);

    /**
     * List measurements according to parameters.
     *
     * @param deviceAddress String
     * @param userId        String
     * @param offset        int
     * @param limit         int
     * @return List<T>
     */
    public List<T> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit);

    /**
     * Retrieves all measurements of a device in accordance with the filters.
     *
     * @param dateStart     long - datetime start
     * @param dateEnd       long - datetime end
     * @param deviceAddress String
     * @param userId        String
     * @return List<T>
     */
    public List<T> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId);

    /**
     * Retrieves all measurements of a device that have not been sent to the remote server.
     *
     * @param deviceAddress String
     * @param userId        String
     * @return List<T>
     */
    public List<T> getNotSent(@NonNull String deviceAddress, @NonNull String userId);

    /**
     * Retrieves all measurements of a device that were sent to the remote server
     *
     * @param deviceAddress String
     * @param userId        String
     * @return List<T>
     */
    public List<T> getWasSent(@NonNull String deviceAddress, @NonNull String userId);
}
