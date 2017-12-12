package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.MeasurementScale;
import br.edu.uepb.nutes.haniot.model.MeasurementScale_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents Measurement ScaleDAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementScaleDAO implements MeasurementDAO<MeasurementScale> {

    public static MeasurementScaleDAO instance;
    private static Box<MeasurementScale> scaleBox;

    private MeasurementScaleDAO() {
    }

    public static synchronized MeasurementScaleDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementScaleDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        scaleBox = boxStore.boxFor(MeasurementScale.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull MeasurementScale o) {
        return scaleBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull MeasurementScale o) {
        if (o.getId() == 0) {
            MeasurementScale measurementUp = get(o.getId());

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
    public boolean remove(@NonNull MeasurementScale o) {
        return scaleBox.query()
                .equal(MeasurementScale_.id, o.getId())
                .build()
                .remove() > 0;
    }

    @Override
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .build()
                .remove();
    }

    @Override
    public MeasurementScale get(@NonNull long id) {
        return scaleBox.query()
                .equal(MeasurementScale_.id, id)
                .build()
                .findFirst();
    }

    @Override
    public List<MeasurementScale> listAll(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .orderDesc(MeasurementScale_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementScale> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .orderDesc(MeasurementScale_.id)
                .build()
                .find(offset, limit);
    }

    @Override
    public List<MeasurementScale> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .between(MeasurementScale_.registrationTime, dateStart, dateEnd)
                .orderDesc(MeasurementScale_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementScale> getNotSent(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .equal(MeasurementScale_.hasSent, 0)
                .build()
                .find();
    }

    @Override
    public List<MeasurementScale> getWasSent(@NonNull String deviceAddress, @NonNull String userId) {
        return scaleBox.query()
                .equal(MeasurementScale_.deviceAddress, deviceAddress)
                .equal(MeasurementScale_.userId, userId)
                .equal(MeasurementScale_.hasSent, 1)
                .build()
                .find();
    }
}
