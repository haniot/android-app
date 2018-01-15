package br.edu.uepb.nutes.haniot;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.MyObjectBox;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalData;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

/**
 * Represents App.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class App extends Application {
    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }

        Historical<Measurement> historical = new Historical.Query()
                .ordination("teste", "ASC")
                .pagination(0, 50)
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .period("1d")
                .params(new Params("13615616","4564651",MeasurementType.TEMPERATURE))
                .context(this)
                .callback(mCallback)
                .build();

        Measurement m1 = new Measurement(33.33344444D, "°C", 1515126555535L, MeasurementType.TEMPERATURE);
        Measurement m2 = new Measurement(37D, "°C", 1515126555535L, MeasurementType.TEMPERATURE);
        Measurement m3 = new Measurement(38D, "°C", 1515126555535L, MeasurementType.TEMPERATURE);
        m1.addMeasurement(m2, m3);
        m1.addContext(new ContextMeasurement(1, 5), new ContextMeasurement(2, 5));

        List<Measurement> list = new ArrayList<>();

        list.add(m1);
        list.add(new Measurement(60.877777D, "kg", 1514775600000L, MeasurementType.BODY_MASS));

        Log.i("TESTE", historical.toString());


        for (Measurement m : list) {
            Log.i("TESTE LOOP 1", m.toString());
            for (Measurement mm : m.getMeasurements()) {
                Log.i("TESTE LOOP 2", mm.toString());
            }
            for (ContextMeasurement cc : m.getContextMeasurements()) {
                Log.i("TESTE LOOP 3", cc.toString());
            }
        }
    }

    Historical.Callback mCallback = new Historical.Callback<List<HistoricalData>>() {

        @Override
        public void onError(@Nullable List<HistoricalData> result) {
            Log.i("mCallback", "onError() " + result);
        }

        @Override
        public void onSuccess(List<HistoricalData> result) {
            Log.i("mCallback", "onResult() " + result.toString());
        }

        @Override
        public void onBeforeSend() {
            Log.i("mCallback", "onBeforeSend()");
        }

        @Override
        public void onAfterSend() {
            Log.i("mCallback", "onAfterSend()");
        }
    };

    public BoxStore getBoxStore() {
        return boxStore;
    }
}