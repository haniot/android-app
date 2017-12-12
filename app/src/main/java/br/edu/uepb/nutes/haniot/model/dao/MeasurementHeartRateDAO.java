package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.MeasurementHeartRate;
import br.edu.uepb.nutes.haniot.model.MeasurementHeartRate_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents Measurement Heart Rate DAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementHeartRateDAO implements MeasurementDAO<MeasurementHeartRate> {
    public static MeasurementHeartRateDAO instance;
    private static Box<MeasurementHeartRate> heartRateBox;

    private MeasurementHeartRateDAO() {
    }

    public static synchronized MeasurementHeartRateDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementHeartRateDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        heartRateBox = boxStore.boxFor(MeasurementHeartRate.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull MeasurementHeartRate o) {
        return heartRateBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull MeasurementHeartRate o) {
        if (o.getId() == 0) {
            MeasurementHeartRate measurementUp = get(o.getId());

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
    public boolean remove(@NonNull MeasurementHeartRate o) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.id, o.getId())
                .build()
                .remove() > 0;
    }

    @Override
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .build()
                .remove();
    }

    @Override
    public MeasurementHeartRate get(@NonNull long id) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.id, id)
                .build()
                .findFirst();
    }

    @Override
    public List<MeasurementHeartRate> listAll(@NonNull String deviceAddress, @NonNull String userId) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .orderDesc(MeasurementHeartRate_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementHeartRate> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .orderDesc(MeasurementHeartRate_.id)
                .build()
                .find(offset, limit);
    }

    @Override
    public List<MeasurementHeartRate> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .between(MeasurementHeartRate_.registrationTime, dateStart, dateEnd)
                .orderDesc(MeasurementHeartRate_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementHeartRate> getNotSent(@NonNull String deviceAddress, @NonNull String userId) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .equal(MeasurementHeartRate_.hasSent, 0)
                .build()
                .find();
    }

    @Override
    public List<MeasurementHeartRate> getWasSent(@NonNull String deviceAddress, @NonNull String userId) {
        return heartRateBox.query()
                .equal(MeasurementHeartRate_.deviceAddress, deviceAddress)
                .equal(MeasurementHeartRate_.userId, userId)
                .equal(MeasurementHeartRate_.hasSent, 1)
                .build()
                .find();
    }
}
