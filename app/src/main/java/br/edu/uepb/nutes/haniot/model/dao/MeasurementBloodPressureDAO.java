package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.MeasurementBloodPressure;
import br.edu.uepb.nutes.haniot.model.MeasurementBloodPressure_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents Measurement BloodPressureDAO.
 *
 * @author Lucas barbosa <lucas.barbosa@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */

public class MeasurementBloodPressureDAO implements MeasurementDAO<MeasurementBloodPressure> {
    public static MeasurementBloodPressureDAO instance;
    private static Box<MeasurementBloodPressure> thermometerBox;

    private MeasurementBloodPressureDAO() {
    }

    public static synchronized MeasurementBloodPressureDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementBloodPressureDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        thermometerBox = boxStore.boxFor(MeasurementBloodPressure.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull MeasurementBloodPressure o) {
        return thermometerBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull MeasurementBloodPressure o) {
        if (o.getId() == 0) {
            MeasurementBloodPressure measurementUp = get(o.getId());

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
    public boolean remove(@NonNull MeasurementBloodPressure o) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.id, o.getId())
                .build()
                .remove() > 0;
    }

    @Override
    public long removeAll(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .build()
                .remove();
    }

    @Override
    public MeasurementBloodPressure get(@NonNull long id) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.id, id)
                .build()
                .findFirst();
    }

    @Override
    public List<MeasurementBloodPressure> listAll(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .orderDesc(MeasurementBloodPressure_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementBloodPressure> list(@NonNull String deviceAddress, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .orderDesc(MeasurementBloodPressure_.id)
                .build()
                .find(offset, limit);
    }

    @Override
    public List<MeasurementBloodPressure> filter(@NonNull long dateStart, @NonNull long dateEnd, @NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .between(MeasurementBloodPressure_.registrationTime, dateStart, dateEnd)
                .orderDesc(MeasurementBloodPressure_.id)
                .build()
                .find();
    }

    @Override
    public List<MeasurementBloodPressure> getNotSent(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .equal(MeasurementBloodPressure_.hasSent, 0)
                .build()
                .find();
    }

    @Override
    public List<MeasurementBloodPressure> getWasSent(@NonNull String deviceAddress, @NonNull String userId) {
        return thermometerBox.query()
                .equal(MeasurementBloodPressure_.deviceAddress, deviceAddress)
                .equal(MeasurementBloodPressure_.userId, userId)
                .equal(MeasurementBloodPressure_.hasSent, 1)
                .build()
                .find();
    }
}
