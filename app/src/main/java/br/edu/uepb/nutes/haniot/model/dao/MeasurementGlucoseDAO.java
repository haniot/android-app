package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents Measurement Glucose DAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementGlucoseDAO implements MeasurementDAO<MeasurementGlucose> {

    public static MeasurementGlucoseDAO instance;
    private static Box<MeasurementGlucose> scaleBox;

    private MeasurementGlucoseDAO() {
    }

    public static synchronized MeasurementGlucoseDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementGlucoseDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        scaleBox = boxStore.boxFor(MeasurementGlucose.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull MeasurementGlucose o) {
        return scaleBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull MeasurementGlucose o) {
        if (o.getId() == 0) {
            MeasurementGlucose measurementUp = get(o.getId());

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
    public boolean remove(@NonNull MeasurementGlucose o) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.id, o.getId())
                .build()
                .remove() > 0;
    }

    @Override
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .build()
                .remove();
    }

    @Override
    public MeasurementGlucose get(@NonNull long id) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.id, id)
                .build()
                .findFirst();
    }

    @Override
    public List<MeasurementGlucose> listAll(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .orderDesc(MeasurementGlucose_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementGlucose> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .orderDesc(MeasurementGlucose_.id)
                .build()
                .find(offset, limit);
    }

    @Override
    public List<MeasurementGlucose> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .between(MeasurementGlucose_.registrationTime, dateStart, dateEnd)
                .orderDesc(MeasurementGlucose_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementGlucose> getNotSent(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .equal(MeasurementGlucose_.hasSent, 0)
                .build()
                .find();
    }

    @Override
    public List<MeasurementGlucose> getWasSent(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementGlucose_.deviceAddress, deviceAddress)
                .equal(MeasurementGlucose_.userId, userId)
                .equal(MeasurementGlucose_.hasSent, 1)
                .build()
                .find();
    }
}
