package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.MeasurementBloodPressure;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose;
import br.edu.uepb.nutes.haniot.model.MeasurementHeartRate;
import br.edu.uepb.nutes.haniot.model.MeasurementScale;
import br.edu.uepb.nutes.haniot.model.MeasurementThermometer;
import br.edu.uepb.nutes.haniot.model.dao.server.Server;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import okhttp3.Headers;

/**
 * Class that implements logic for synchronizing local data with the remote server.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SynchronizationServer {
    private Session session;
    private Context context;
    private List<Object> measurementsObject;

    public SynchronizationServer(Context context) {
        session = new Session(context);
        this.context = context;
    }

    /**
     *
     * @param callbackSynchronization
     */
    public void run(SynchronizationServer.Callback callbackSynchronization) {
        // User not connected or does not have active internet connection
        if (session.getIdLogged() == null || !ConnectionUtils.internetIsEnabled(context)) {
            callbackSynchronization.onError(null);
            return;
        }

        measurementsObject = new ArrayList<>();

        // List with devices associated with the logged in user
        List<Device> devicesUser = DeviceClientDAO.getInstance(context).listAll(session.getIdLogged());

        /**
         * Get all measurements of user-associated devices
         */
        for (Device d : devicesUser) {
            // Thermomenter
            measurementsObject.addAll(MeasurementThermometerDAO.getInstance(context).getNotSent(d.getAddress(), session.getIdLogged()));
            // Scale
            measurementsObject.addAll(MeasurementScaleDAO.getInstance(context).getNotSent(d.getAddress(), session.getIdLogged()));
            // Heart Rate
            measurementsObject.addAll(MeasurementHeartRateDAO.getInstance(context).getNotSent(d.getAddress(), session.getIdLogged()));
            // Glucose
            measurementsObject.addAll(MeasurementGlucoseDAO.getInstance(context).getNotSent(d.getAddress(), session.getIdLogged()));
            // Blood Pressure
            measurementsObject.addAll(MeasurementBloodPressureDAO.getInstance(context).getNotSent(d.getAddress(), session.getIdLogged()));
        }

        prepareMeasurements(measurementsObject, callbackSynchronization);
    }

    /**
     * Send measurement to server.
     */
    private void prepareMeasurements(List<Object> measurements, SynchronizationServer.Callback callbackSynchronization) {
        Log.i("TOTAL", measurements.size() + "");

        JsonObject jsonMeasurements = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (final Object o : measurements) {
            JsonObject jsonMeasurement = processTypeMeasurement(o);

            /**
             * Mount the json to send to the server
             */
            jsonMeasurement.remove("hasSent"); // Removes unnecessary data for server
            jsonArray.add(jsonMeasurement);
        }

        jsonMeasurements.add("measurements", jsonArray);

        /**
         * There is no data to send...
         */
        if (measurements.isEmpty() || jsonMeasurements == null) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("message",
                        context.getResources().getString(R.string.synchronization_no_data_to_send));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            callbackSynchronization.onSuccess(jsonObject);
        } else {
            sendMeasurementToServer(jsonMeasurements.toString(), callbackSynchronization);
        }
    }

    /**
     * Treats the object of a measurement by casting for the specific type and returns a JsonObject.
     *
     * @param o Object
     * @return JsonObject
     */
    private JsonObject processTypeMeasurement(Object o) {
        GsonBuilder gson = new GsonBuilder();
        JsonObject jsonMeasurement = null;

        if (o instanceof MeasurementThermometer) {
            MeasurementThermometer m = (MeasurementThermometer) o;
            jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
        } else if (o instanceof MeasurementScale) {
            MeasurementScale m = (MeasurementScale) o;
            jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
        } else if (o instanceof MeasurementHeartRate) {
            MeasurementHeartRate m = (MeasurementHeartRate) o;
            jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
        } else if (o instanceof MeasurementBloodPressure) {
            MeasurementBloodPressure m = (MeasurementBloodPressure) o;
            jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
        } else if (o instanceof MeasurementGlucose) {
            MeasurementGlucose m = (MeasurementGlucose) o;
            jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
        }

        return jsonMeasurement;
    }

    /**
     * Sends all measurements not sent to the server.
     *
     * @param jsonMeasurements        String
     * @param callbackSynchronization SynchronizationServer.Callback
     */
    private void sendMeasurementToServer(String jsonMeasurements, final SynchronizationServer.Callback callbackSynchronization) {
        Log.i("jsonMeasurements", jsonMeasurements.toString());

        /**
         * Get the required user token in request authentication
         */
        Headers headers = new Headers.Builder()
                .add("Authorization", "JWT ".concat(new Session(context).getTokenLogged()))
                .build();

        /**
         * Send to server
         */
        Server.getInstance(context).post("measurements", jsonMeasurements, headers, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                callbackSynchronization.onError(result);
            }

            @Override
            public void onSuccess(JSONObject result) {
                // Sending successfully, we can remove from the database these measurements
                removeAllMeasurements();

                callbackSynchronization.onSuccess(result);
            }
        });
    }

    /**
     * Removes all measurements associated with the devices of the logged in user.
     */
    private void removeAllMeasurements() {
        for (final Object o : measurementsObject) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (o instanceof MeasurementThermometer) {
                        MeasurementThermometer m = (MeasurementThermometer) o;
                        MeasurementThermometerDAO.getInstance(context).removeAll(m.getDeviceAddress(), m.getUserId());
                    } else if (o instanceof MeasurementScale) {
                        MeasurementScale m = (MeasurementScale) o;
                        MeasurementScaleDAO.getInstance(context).removeAll(m.getDeviceAddress(), m.getUserId());
                    } else if (o instanceof MeasurementHeartRate) {
                        MeasurementHeartRate m = (MeasurementHeartRate) o;
                        MeasurementHeartRateDAO.getInstance(context).removeAll(m.getDeviceAddress(), m.getUserId());
                    } else if (o instanceof MeasurementBloodPressure) {
                        MeasurementBloodPressure m = (MeasurementBloodPressure) o;
                        MeasurementBloodPressureDAO.getInstance(context).removeAll(m.getDeviceAddress(), m.getUserId());
                    } else if (o instanceof MeasurementGlucose) {
                        MeasurementGlucose m = (MeasurementGlucose) o;
                        MeasurementGlucoseDAO.getInstance(context).removeAll(m.getDeviceAddress(), m.getUserId());
                    }
                }
            });
        }
    }

    /**
     * Callback to return from the remote server
     */
    public interface Callback {
        void onError(JSONObject result);

        void onSuccess(JSONObject result);
    }
}
