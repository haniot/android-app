package br.edu.uepb.nutes.haniot.server.historic;

import android.content.Context;

import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.server.Server;

/**
 * Module to perform measurements queries on the server.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HistoricMeasurement {
    private final String LOG = "Server";
    private static Context mContext;

    private int type;

    private HistoricMeasurement() {

    }

    public static synchronized HistoricMeasurement builder(Context context) {
        mContext = context;
//        return new HistoricMeasurement();
    }

    public HistoricMeasurement type(int type, Callback c) {
        return this;
    }

    /**
     * Callback to return from the remote server
     */
    public interface Callback<T> {
        void onError(T result);
        void onResult(T result);
        void onBeforeSend();
        void onAfterSend();
    }
}

