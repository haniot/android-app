package br.edu.uepb.nutes.haniot.model.dao;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * DeviceDAO interface.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface DeviceDAO<T> {

    /**
     * Adds a new device to the database.
     *
     * @param o
     * @return boolean
     */
    public boolean save(@NonNull T o);

    /**
     * Update device data according to id.
     *
     * @param o
     * @return boolean
     */
    public boolean update(@NonNull T o);

    /**
     * Removes device passed as parameter.
     *
     * @param o
     * @return boolean
     */
    public boolean remove(@NonNull T o);

    /**
     * Removes all devices.
     *
     * @param userId String
     * @return long - Total number of items removed
     */
    public long removeAll(@NonNull String userId);

    /**
     * Select a Device.
     *
     * @param address String
     * @param userId  String
     * @return Object
     */
    public T get(@NonNull String address, @NonNull String userId);

    /**
     * Retrieves all device.
     *
     * @param userId String
     * @return List<T>
     */
    public List<T> listAll(@NonNull String userId);
}