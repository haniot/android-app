package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.MeasurementThermometer;
import br.edu.uepb.nutes.haniot.model.MeasurementThermometer_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents Measurement Thermometer DAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementThermometerDAO implements MeasurementDAO<MeasurementThermometer> {
    public static MeasurementThermometerDAO instance;
    private static Box<MeasurementThermometer> thermometerBox;

    private MeasurementThermometerDAO() {
    }

    public static synchronized MeasurementThermometerDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementThermometerDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        thermometerBox = boxStore.boxFor(MeasurementThermometer.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull MeasurementThermometer o) {
        Log.i("SAVING", o.toString());
        return thermometerBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull MeasurementThermometer o) {
        if (o.getId() == 0) {
            MeasurementThermometer measurementUp = get(o.getId());

            /**
             * Id is required for an update
             * Otherwise it will be an insert
             */
            if (measurementUp == null) return false;

            o.setId(measurementUp.getId());
        }

        return save(o); // update
    }

    @Override
    public boolean remove(@NonNull MeasurementThermometer o) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.id, o.getId())
                .build()
                .remove() > 0;
    }

    @Override
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .build()
                .remove();
    }

    @Override
    public MeasurementThermometer get(@NonNull long id) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.id, id)
                .build()
                .findFirst();
    }

    @Override
    public List<MeasurementThermometer> listAll(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .orderDesc(MeasurementThermometer_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementThermometer> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .orderDesc(MeasurementThermometer_.id)
                .build()
                .find(offset, limit);
    }

    @Override
    public List<MeasurementThermometer> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .between(MeasurementThermometer_.registrationTime, dateStart, dateEnd)
                .orderDesc(MeasurementThermometer_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementThermometer> getNotSent(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .equal(MeasurementThermometer_.hasSent, 0)
                .build()
                .find();
    }

    @Override
    public List<MeasurementThermometer> getWasSent(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementThermometer_.deviceAddress, deviceAddress)
                .equal(MeasurementThermometer_.userId, userId)
                .equal(MeasurementThermometer_.hasSent, 1)
                .build()
                .find();
    }
}
