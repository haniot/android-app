package br.edu.uepb.nutes.haniot.server;

import android.content.Context;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;

/**
 * Class that implements logic for synchronizing local data with the remote server.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SynchronizationServer {
    private final String TAG = "SynchronizationServer";

    private static SynchronizationServer instance;
    private static Session session;
    public static Context context;

    private SynchronizationServer() {
    }

    public static synchronized SynchronizationServer getInstance(Context c) {
//        if (instance == null) {
//            instance = new SynchronizationServer();
//            session = new Session(c);
//            context = c;
//        }

        return instance;
    }

    /**
     * Execute Synchronization with callback
     *
     * @param callbackSynchronization
     */
    public void run(SynchronizationServer.Callback callbackSynchronization) {
        // User not connected or does not have active internet connection
//        if (!session.isLogged() || !ConnectionUtils.internetIsEnabled(context)) {
//            if (callbackSynchronization != null)
//                callbackSynchronization.onError(null);
//
//            return;
//        }

//        List<Measurement> measurements = MeasurementDAO.getInstance(context).getNotSent(session.getIdLogged());
//        sendMeasurementToServer(prepareMeasurements(measurements), callbackSynchronization);
    }

    /**
     * Execute Synchronization no callback
     */
    public void run() {
        run(null);
    }

    /**
     * Prepare neasurements
     *
     * @param measurements
     * @return String json
     */
    private String prepareMeasurements(List<Measurement> measurements) {
        // No data send
//        if (measurements == null || measurements.isEmpty()) {
//            return null;
//        }
//
//        JsonArray resultArrayJson = new JsonArray();
//        List<Measurement> measurementsNoRelations = new ArrayList<>();
//        List<Measurement> measurementsWithRelations = new ArrayList<>();

//        /**
//         * Separates the measurements that have self-relation from those that do not have.
//         */
//        for (Measurement m : measurements) {
//            if (m.getMeasurementList().size() > 0) {
//                boolean noAdd = false;
//                for (Measurement mr : m.getMeasurementList()) {
//                    if (measurementsWithRelations.contains(mr)) noAdd = true;
//                    break;
//                }
//                if (!noAdd) measurementsWithRelations.add(m);
//            } else {
//                measurementsNoRelations.add(m);
//            }
//        }
//
//        /**
//         * To treat the relationships and remove from the final json
//         * those that are present in the association.
//         */
//        for (Measurement m : measurementsWithRelations) {
//            JsonArray jsonRelationsArray = new JsonArray();
//            for (Measurement mRelation : m.getMeasurementList()) {
//                jsonRelationsArray.add(processMeasurement(mRelation));
//                measurementsNoRelations.remove(mRelation);
//            }
//            JsonObject jsonMeasurement = processMeasurement(m);
//            jsonMeasurement.add("measurements", jsonRelationsArray);
//            resultArrayJson.add(jsonMeasurement); // Add to json final
//        }
//
//        /**
//         * Treat those that have no relationship and that were not added to the final json.
//         */
//        for (Measurement m : measurementsNoRelations) {
//            JsonObject jsonMeasurement = processMeasurement(m);
//            jsonMeasurement.add("measurements", new JsonArray());
//            resultArrayJson.add(jsonMeasurement); // Add to json final
//        }

        /**
         * Mount the json to send to the server
         */
//        JsonObject resultJson = new JsonObject();
//        resultJson.add("measurements", resultArrayJson);
//        Log.i(TAG, "RESULT_JSON: " + resultJson.toString());
//
//        return resultJson.toString();
        return "";
    }

    /**
     * Treats the object of a measurement by casting for the specific type and returns a JsonObject.
     *
     * @param m Measurement
     * @return JsonObject
     */
    private JsonObject processMeasurement(Measurement m) {
        JsonObject result = new JsonObject();
//
//        /**
//         * Required
//         */
//        result.addProperty("value", m.getValue());
//        result.addProperty("unit", m.getUnit());
//        result.addProperty("registrationDate", m.getRegistrationDate());
//        result.addProperty("typeId", m.getTypeId());
//        result.addProperty("userId", m.getUserObj().get_id());
//        if (m.getDevice().getTarget() != null) {
//            result.addProperty("deviceId", m.getDevice().getTarget().get_id());
//        }
//        result.add("contexts", processContextMeasurement(m.getContextMeasurements()));

        return result;
    }

//    /**
//     * @param contextMeasurements
//     * @return JsonArray
//     */
//    private JsonArray processContextMeasurement(List<ContextMeasurement> contextMeasurements) {
//        JsonArray result = new JsonArray();
//
//        if (contextMeasurements != null) {
//            for (ContextMeasurement c : contextMeasurements) {
//                JsonObject o = new JsonObject();
//                o.addProperty("valueId", c.getValueId());
//                o.addProperty("typeId", c.getTypeId());
//
//                result.add(o);
//            }
//        }
//
//        return result;
//    }

    /**
     * Sends all measurements not sent to the server.
     *
     * @param jsonMeasurements        String
     * @param callbackSynchronization SynchronizationServer.Callback
     */
    private void sendMeasurementToServer(String jsonMeasurements, final SynchronizationServer.Callback callbackSynchronization) {
//        /**
//         * There is no data to send...
//         */
//        if (jsonMeasurements == null || jsonMeasurements.isEmpty()) {
//            if (callbackSynchronization == null)
//                return;
//
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("message",
//                        context.getResources().getString(R.string.synchronization_no_data_to_send));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            callbackSynchronization.onSuccess(jsonObject);
//            return;
//        }
//
//        /**
//         * Send to server
//         * /measurements/users/:userId
//         */
//        Server.getInstance(context).post("measurements/users/" + session.getUserLogged().get_id(),
//                jsonMeasurements, new Server.Callback() {
//                    @Override
//                    public void onError(JSONObject result) {
//                        if (callbackSynchronization != null)
//                            callbackSynchronization.onError(result);
//                    }
//
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        // Sending successfully, we can remove from the database these measurements
//                        removeAllMeasurements();
//
//                        if (callbackSynchronization != null)
//                            callbackSynchronization.onSuccess(result);
//                    }
//                });
    }

    /**
     * Removes all measurements and their associations.
     */
    private void removeAllMeasurements() {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "removeAllMeasurements()");
////                ContextMeasurementDAO contextMeasurementDAO = ContextMeasurementDAO.getInstance(context);
////                MeasurementDAO measurementDAO = MeasurementDAO.getInstance(context);
////                for (Measurement m : measurementDAO.getNotSent(session.getIdLogged())) {
////                    if (m.getContextMeasurements().size() > 0)
////                        contextMeasurementDAO.removeAllOfMeasurement(m.getId());
////                }
////                measurementDAO.removeAll(session.getIdLogged());
//            }
//        });
    }

    /**
     * Callback to return from the remote server
     */
    public interface Callback {
        void onError(JSONObject result);

        void onSuccess(JSONObject result);
    }
}
