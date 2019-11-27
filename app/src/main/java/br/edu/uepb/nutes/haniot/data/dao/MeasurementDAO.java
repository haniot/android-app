package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

/**
 * MeasurementDAO implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementDAO {
    private static final String TAG = "MEASUREMENTDAO";
    private static MeasurementDAO instance;
    private static Box<MeasurementOB> measurementBox;

    private MeasurementDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        measurementBox = boxStore.boxFor(MeasurementOB.class);
    }

    public static synchronized MeasurementDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new MeasurementDAO(context);
        return instance;
    }

    /**
     * Add a new measurement to the database.
     *
     * @param measurement Measurement
     * @return Long - new ID of Measurement
     */
    public long save(@NonNull Measurement measurement) {
        Log.i(TAG, "save: ");
        if (contains(measurement))
            return 0; // Não adiciona measurement repetida
        return measurementBox.put(Convert.convertMeasurement(measurement));
    }

    /**
     * Verify if measurement is in measurementBox
     *
     * @param m Measurement
     * @return True if contains or False otherwise
     */
    private boolean contains(@NonNull Measurement m) {
        MeasurementOB first = measurementBox.query()
                .equal(MeasurementOB_.user_id, m.getUser_id())
                .equal(MeasurementOB_.userId, m.getUserId())
                .equal(MeasurementOB_.type, m.getType())
                .equal(MeasurementOB_.timestamp, m.getTimestamp())
                .build()
                .findFirst();
        return first != null;
    }

    /**
     * Update measurementOB data.
     *
     * @param measurement Measurement
     * @return boolean
     */
    public long update(@NonNull Measurement measurement) {
        Log.i(TAG, "update: ");
        if (measurement.getId() == 0)
            return 0;
        return save(measurement); // updateOrSave
    }

    /**
     * Remove measurement
     *
     * @param measurementId long
     * @return boolean
     */
    public boolean remove(long measurementId) {
        Log.i(TAG, "remove: ");
        return measurementBox.query()
                .equal(MeasurementOB_.id, measurementId)
                .build()
                .remove() > 0;
    }

    /**
     * Remove all measurements associated with user.
     *
     * @param patientId long
     */
    public void removeByPatientId(long patientId) {
        Log.i(TAG, "removeByPatientId: ");
        measurementBox.query()
                .equal(MeasurementOB_.userId, patientId)
                .build()
                .remove();
    }

    /**
     * Get measurements by type
     *
     * @param patient   Patient
     * @param type      String
     * @param sort      String
     * @param dateStart String
     * @param dateEnd   String
     * @param page      Int
     * @param limit     Int
     * @return List of Measurement
     */
    public List<Measurement> getMeasurementsByType(Patient patient, String type, String sort, String dateStart, String dateEnd, int page, int limit) {
        Log.i(TAG, "getMeasurementsByType: ");

        boolean desc = false;
        if (sort != null && '-' == sort.charAt(0)) desc = true;

        QueryBuilder<MeasurementOB> query = measurementBox.query();

        if (patient.get_id() != null) {
            query.equal(MeasurementOB_.user_id, patient.get_id())
                    .equal(MeasurementOB_.type, type);
//                        .filter(m -> DateUtils.compareDates(m.getTimestamp(), dateStart, dateEnd)) nao suportado
        } else {
            query.equal(MeasurementOB_.userId, patient.getId())
                    .equal(MeasurementOB_.type, type);
        }
        if (desc) query.orderDesc(MeasurementOB_.timestamp);
        else query.order(MeasurementOB_.timestamp);

        List<MeasurementOB> aux = query
                .build()
                .find((page - 1) * limit, limit);
        return Convert.listMeasurementsToModel(aux);
    }

    public List<Measurement> getAllNotSync() {
        Log.i(TAG, "getAllNotSync: ");
        List<MeasurementOB> aux =
                measurementBox.query()
                        .equal(MeasurementOB_.sync, false)
                        .build()
                        .find();
        return Convert.listMeasurementsToModel(aux);
    }

    public List<Measurement> getAllNotSync(long id) {
        Log.i(TAG, "getAllNotSync: ");
        List<MeasurementOB> aux =
                measurementBox.query()
                        .equal(MeasurementOB_.userId, id)
                        .equal(MeasurementOB_.sync, false)
                        .build()
                        .find();
        return Convert.listMeasurementsToModel(aux);
    }

    public void markAsSync(String patient_id, String type, String timestamp) {
        Log.i(TAG, "remove: ");
        measurementBox.query()
                .equal(MeasurementOB_.user_id, patient_id)
                .equal(MeasurementOB_.type, type)
                .equal(MeasurementOB_.timestamp, timestamp)
                .build()
                .remove();
    }

    public void removeSyncronized(@NonNull String patient_id) {
        Log.i(TAG, "removeSyncronized: ");
        measurementBox.query()
                .equal(MeasurementOB_.sync, true)
                .equal(MeasurementOB_.user_id, patient_id)
                .build()
                .remove();
    }
}
