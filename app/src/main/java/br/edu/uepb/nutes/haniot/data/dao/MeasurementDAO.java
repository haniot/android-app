package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB_;
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
    private static final String TAG = "MEASUREMENTDAO";
    public static MeasurementDAO instance;
    private static Box<MeasurementOB> measurementBox;

    private MeasurementDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        measurementBox = boxStore.boxFor(MeasurementOB.class);
    }

    public static synchronized MeasurementDAO getInstance(@NonNull Context context) {
        if (instance == null)
            instance = new MeasurementDAO(context);
        return instance;
    }

    /**
     * Adds a new measurementOB to the database.
     *
     * @param measurement
     * @return boolean
     */
    public long save(@NonNull Measurement measurement) {
        return measurementBox.put(Convert.convertMeasurement(measurement));
    }

    /**
     * Update measurementOB data.
     *
     * @param measurement
     * @return boolean
     */
    public long update(@NonNull Measurement measurement) {
        if (measurement.getId() == 0) {
            /**
             * Id is required for an updateOrSave
             * Otherwise it will be an insert
             */
            return 0;
        }
        return save(measurement); // updateOrSave
    }

    /**
     * Remove measurementOB.
     *
//     * @param userId        String
     * @param measurementId long
     * @return boolean
     */
    public boolean remove(@NonNull long measurementId) {
        return measurementBox.query()
                .equal(MeasurementOB_.id, measurementId)
//                .equal(MeasurementOB_.userId, userId)
                .build()
                .remove() > 0;
    }

//    /**
//     * Remove all measurements associated with device and user.
//     *
//     * @param deviceId long
//     * @param userId   long
//     * @return boolean
//     */
//    public boolean removeAll(@NonNull long deviceId, @NonNull long userId) {
//        return measurementBox.query()
//                .equal(MeasurementOB_.deviceId, deviceId)
//                .equal(MeasurementOB_.userId, userId)
//                .build()
//                .remove() > 0;
//    }

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

//    public Measurement get(String userId, String measurementId) {
//        MeasurementOB m = measurementBox.query()
//                .equal(MeasurementOB_._id, measurementId)
//                .equal(MeasurementOB_.userId, userId)
//                .build()
//                .findFirst();
//        return Convert.convertMeasurement(m);
//    }

//    /**
//     * Select all measurements associated with the user.
//     *
//     * @param userId long
//     * @param offset int
//     * @param limit  int
//     * @return List<MeasurementOB>
//     */
//    public List<MeasurementOB> getAllByUserId(@NonNull long userId, @NonNull int offset, @NonNull int limit) {
//        return measurementBox.query()
//                .equal(MeasurementOB_.userId, userId)
//                .orderDesc(MeasurementOB_.id)
//                .build()
//                .find(offset, limit);
//    }

//    /**
//     * Select all measurements of a type associated with the user.
//     *
//     * @param type   {@link String}
//     * @param userId long
//     * @param offset int
//     * @param limit  int
//     * @return List<MeasurementOB>
//     */
//    public List<MeasurementOB> getAllByUserId(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
//        return measurementBox.query()
//                .equal(MeasurementOB_.type, type)
//                .equal(MeasurementOB_.userId, userId)
//                .orderDesc(MeasurementOB_.timestamp)
//                .build()
//                .find(offset, limit);
//    }

//    /**
//     * Select all measurements associated with the device and user.
//     *
//     * @param deviceId long
//     * @param userId   long
//     * @param offset   int
//     * @param limit    int
//     * @return List<MeasurementOB>
//     */
//    public List<MeasurementOB> getAllByUserId(@NonNull long deviceId, @NonNull long userId, @NonNull int offset, @NonNull int limit) {
//        return measurementBox.query()
//                .equal(MeasurementOB_.deviceId, deviceId)
//                .equal(MeasurementOB_.userId, userId)
//                .orderDesc(MeasurementOB_.id)
//                .build()
//                .find(offset, limit);
//    }

    public List<Measurement> getAllMeasurements(String userId, String sort, String dateStart, String dateEnd, int page, int limit) {
        page--;
        List<MeasurementOB> aux;
        if (userId == null) {
            aux = measurementBox.query()
                    .orderDesc(MeasurementOB_.timestamp)
                    .build()
                    .find(page * limit, limit);
        } else {
            aux = measurementBox.query()
                    .equal(MeasurementOB_.userId, userId)
                    .orderDesc(MeasurementOB_.timestamp)
                    .build()
                    .find(page * limit, limit);
        }
        return Convert.listMeasurementsToModel(aux);
    }

    public List<Measurement> getMeasurementsByType(String userId, String type, String sort, String dateStart, String dateEnd, int page, int limit) {
        page--;
        List<MeasurementOB> aux =
                measurementBox.query()
                        .equal(MeasurementOB_.userId, userId)
                        .equal(MeasurementOB_.type, type)
                        .orderDesc(MeasurementOB_.timestamp)
//                        .filter(m -> DateUtils.compareDates(m.getTimestamp(), dateStart, dateEnd))
                        .build()
                        .find(page * limit, limit);
        List<MeasurementOB> teste = measurementBox.query()
                .orderDesc(MeasurementOB_.timestamp)
                .build().find(page * limit, limit);
        Log.i(TAG, "ATUALMENTE: " + teste.toString());
        return Convert.listMeasurementsToModel(aux);
    }

    public List<Measurement> getAllNotSync() {
        List<MeasurementOB> aux =
                measurementBox.query()
                        .equal(MeasurementOB_.sync, false)
                        .build()
                        .find();
        return Convert.listMeasurementsToModel(aux);
    }

    public void markAsSync(long id) {
        measurementBox.query()
                .equal(MeasurementOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized() {
        measurementBox.query()
                .equal(MeasurementOB_.sync, true)
                .build()
                .remove();
    }

    public void addAll(List<Measurement> measurements) {
        for (Measurement measurement : measurements) {
            measurement.setSync(true);
            measurementBox.put(new MeasurementOB(measurement));
        }
    }
}
