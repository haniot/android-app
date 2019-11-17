package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
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
        if (measurement.getId() == 0)
            return 0;
        return save(measurement); // updateOrSave
    }

    /**
     * Remove measurement
     * @param measurementId long
     * @return boolean
     */
    public boolean remove(@NonNull long measurementId) {
        return measurementBox.query()
                .equal(MeasurementOB_.id, measurementId)
                .build()
                .remove() > 0;
    }

    /**
     * Remove all measurements associated with user.
     *
     * @param patientId long
     * @return boolean
     */
    public boolean removeByPatientId(@NonNull long patientId) {
        return measurementBox.query()
                .equal(MeasurementOB_.userId, patientId)
                .build()
                .remove() > 0;
    }

    public List<Measurement> getAllMeasurements(String user_id, String sort, String dateStart, String dateEnd, int page, int limit) {
        page--;
        List<MeasurementOB> aux;
        if (user_id == null) {
            aux = measurementBox.query()
                    .orderDesc(MeasurementOB_.timestamp)
                    .build()
                    .find(page * limit, limit);
        } else {
            aux = measurementBox.query()
                    .equal(MeasurementOB_.user_id, user_id)
                    .orderDesc(MeasurementOB_.timestamp)
                    .build()
                    .find(page * limit, limit);
        }
        return Convert.listMeasurementsToModel(aux);
    }

    public List<Measurement> getMeasurementsByType(Patient patient, String type, String sort, String dateStart, String dateEnd, int page, int limit) {
        page--;
        List<MeasurementOB> aux;

        if (patient.get_id() != null) {
            Log.i(TAG, "getMeasurementsByType: COM _ID - " + patient.get_id());
            aux = measurementBox.query()
                    .equal(MeasurementOB_.user_id, patient.get_id())
                    .equal(MeasurementOB_.type, type)
                    .orderDesc(MeasurementOB_.timestamp)
//                        .filter(m -> DateUtils.compareDates(m.getTimestamp(), dateStart, dateEnd))
                    .build()
                    .find(page * limit, limit);
            Log.i(TAG, "getMeasurementsByType: AUX" + aux.toString());
        } else {
            Log.i(TAG, "getMeasurementsByType: SEM _ID");
            aux = measurementBox.query()
                    .equal(MeasurementOB_.userId, patient.getId())
                    .equal(MeasurementOB_.type, type)
                    .orderDesc(MeasurementOB_.timestamp)
//                        .filter(m -> DateUtils.compareDates(m.getTimestamp(), dateStart, dateEnd))
                    .build()
                    .find(page * limit, limit);
        }

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

    public List<Measurement> getAllNotSync(long id) {
        List<MeasurementOB> aux =
                measurementBox.query()
                        .equal(MeasurementOB_.userId, id)
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
        Log.i(Repository.TAG, "Removendo Measurements sincronizadas");
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
