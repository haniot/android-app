package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents ContextMeasurementDAO.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.6
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ContextMeasurementDAO {

    public static ContextMeasurementDAO instance;
    private static Box<ContextMeasurement> contextBox;

    private ContextMeasurementDAO() {
    }

    public static synchronized ContextMeasurementDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new ContextMeasurementDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        contextBox = boxStore.boxFor(ContextMeasurement.class);

        return instance;
    }

    /**
     * Remove all contexts from a measure.
     *
     * @param measurementId long
     * @return boolean
     */
    public boolean removeAllOfMeasurement(@NonNull long measurementId) {
        return contextBox.query()
                .equal(ContextMeasurement_.measurementId, measurementId)
                .build()
                .remove() > 0;
    }
}
