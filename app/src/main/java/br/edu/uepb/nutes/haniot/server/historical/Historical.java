package br.edu.uepb.nutes.haniot.server.historical;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.elderly.Item;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.NameColumnsDB;

/**
 * Module to perform measurements queries on the server.
 *
 * @param <T>
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class Historical<T> {
    private final String urn;
    private final String queryStrings;
    private final int type;
    private Session session;

    private Historical(Query query) {
        this.urn = query.urn;
        this.queryStrings = query.queryStrings;
        this.type = query.type;
    }

    /**
     * Perform request.
     *
     * @param context  Context
     * @param callback CallbackHistorical<T>
     */
    public void request(Context context, CallbackHistorical<T> callback) {
        if (callback == null) throw new IllegalArgumentException("callback is required!");
        if (context == null) throw new IllegalArgumentException("context is required!");

        callback.onBeforeSend();
        this.session = new Session(context);
        String path = this.urn.concat(this.queryStrings);
        Log.i("HISTORICAL", path);

        Server.getInstance(context).get(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                callback.onAfterSend();
                callback.onError(result);
            }

            @Override
            public void onSuccess(JSONObject result) {
                callback.onAfterSend();
                callback.onResult(buildObjects(result));
            }
        });
    }

    /**
     * Construct the objects according to the type of search.
     *
     * @param data JSONObject
     * @return List<T>
     */
    private List<T> buildObjects(JSONObject data) {
        List<T> result = new ArrayList<>();

        try {
            if (type == HistoricalType.MEASUREMENTS_USER || type == HistoricalType.MEASUREMENTS_TYPE_USER
                    || type == HistoricalType.MEASUREMENTS_DEVICE_USER) {
                if (data.has(NameColumnsDB.MEASUREMENT)) {
                    JSONArray arrayData = data.getJSONArray(NameColumnsDB.MEASUREMENT);
                    for (int i = 0; i < arrayData.length(); i++)
                        result.add((T) buildMeasurement(arrayData.getJSONObject(i)));
                }
            } else if (type == HistoricalType.ELDERLIES_USER) {
                if (data.has(NameColumnsDB.ELDERLY)) {
                    JSONArray arrayData = data.getJSONArray(NameColumnsDB.ELDERLY);
                    for (int i = 0; i < arrayData.length(); i++)
                        result.add((T) buildElderly(arrayData.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Construct object of the measurement type from the JSONObject.
     *
     * @param o JSONObject
     * @return Measurement
     * @throws JSONException
     */
    private Measurement buildMeasurement(JSONObject o) throws JSONException {
        Measurement m = new Measurement(
                o.getDouble(NameColumnsDB.MEASUREMENT_VALUE),
                o.getString(NameColumnsDB.MEASUREMENT_UNIT),
                o.getLong(NameColumnsDB.MEASUREMENT_REGISTRATION_DATE),
                o.getInt(NameColumnsDB.MEASUREMENT_TYPE_ID)
        );

        if (o.has(NameColumnsDB.CONTEXT))
            m.addContext(buildContextMeasurement(o.getJSONArray(NameColumnsDB.CONTEXT)));

        if (o.has(NameColumnsDB.MEASUREMENT))
            m.addMeasurement(buildMeasurements(o.getJSONArray(NameColumnsDB.MEASUREMENT)));

        return m;
    }

    /**
     * Construct objects of the measurement type from the JSONArray.
     *
     * @param arrayData JSONArray
     * @return List<Measurement>
     * @throws JSONException
     */
    private List<Measurement> buildMeasurements(JSONArray arrayData) throws JSONException {
        List<Measurement> result = new ArrayList<>();

        for (int i = 0; i < arrayData.length(); i++) {
            JSONObject o = arrayData.getJSONObject(i);
            result.add(buildMeasurement(o));
        }

        return result;
    }

    /**
     * Construct objects of the context measurement type from the JSONArray.
     *
     * @param arrayData JSONArray
     * @return List<ContextMeasurement>
     * @throws JSONException
     */
    private List<ContextMeasurement> buildContextMeasurement(JSONArray arrayData) throws JSONException {
        List<ContextMeasurement> result = new ArrayList<>();

        for (int i = 0; i < arrayData.length(); i++) {
            JSONObject o = arrayData.getJSONObject(i);

            result.add(new ContextMeasurement(
                    o.getInt(NameColumnsDB.CONTEXT_VALUE_ID),
                    o.getInt(NameColumnsDB.CONTEXT_TYPE_ID)
            ));
        }

        return result;
    }

    /**
     * Construct object of the elderly type from the JSONObject.
     *
     * @param o JSONArray
     * @return Elderly
     * @throws JSONException
     */
    private Elderly buildElderly(JSONObject o) throws JSONException {
        Elderly e = new Elderly(
                o.getString(NameColumnsDB.ELDERLY_NAME),
                o.getLong(NameColumnsDB.ELDERLY_DATE_BIRTH),
                o.getDouble(NameColumnsDB.ELDERLY_WEIGHT),
                o.getInt(NameColumnsDB.ELDERLY_HEIGHT),
                o.getInt(NameColumnsDB.ELDERLY_SEX),
                o.getInt(NameColumnsDB.ELDERLY_MARITAL_STATUS),
                o.getInt(NameColumnsDB.ELDERLY_DEGREE_EDUCATION),
                o.getInt(NameColumnsDB.ELDERLY_FALL_RISK),
                o.getString(NameColumnsDB.ELDERLY_PHONE),
                o.getBoolean(NameColumnsDB.ELDERLY_LIVE_ALONE)
        );
        e.setPin(o.getString(NameColumnsDB.ELDERLY_DEVICE_PIN));
        e.set_id(o.getString(NameColumnsDB.ELDERLY_ID));

        if (o.has(NameColumnsDB.ELDERLY_MEDICATIONS)) {
            JSONArray arrayMedications = o.getJSONArray(NameColumnsDB.ELDERLY_MEDICATIONS);
            for (int i = 0; i < arrayMedications.length(); i++) {
                JSONObject objMedication = arrayMedications.getJSONObject(i);
                Item m = new Item(
                        objMedication.getInt(NameColumnsDB.ELDERLY_ITEMS_INDEX),
                        objMedication.getString(NameColumnsDB.ELDERLY_ITEMS_NAME));
                e.addMedication(m);
            }
        }

        if (o.has(NameColumnsDB.ELDERLY_ACCESSORIES)) {
            JSONArray arrayAccessories = o.getJSONArray(NameColumnsDB.ELDERLY_ACCESSORIES);
            for (int i = 0; i < arrayAccessories.length(); i++) {
                JSONObject objMedication = arrayAccessories.getJSONObject(i);
                Item a = new Item(
                        objMedication.getInt(NameColumnsDB.ELDERLY_ITEMS_INDEX),
                        objMedication.getString(NameColumnsDB.ELDERLY_ITEMS_NAME));
                e.addAccessory(a);
            }
        }
        e.setUser(session.getUserLogged());

        return e;
    }

    public static class Query {
        final char SEPARATOR = '&';
        int skip;
        int limit;
        long gt;
        long gte;
        long lt;
        long lte;
        int type;
        String sort;
        String order;
        String filterColumn;
        String dateStart;
        String dateEnd;
        String period;
        String urn;
        String queryStrings;
        Params params;

        public Query() {
            this.sort = NameColumnsDB.MEASUREMENT_REGISTRATION_DATE;
            this.order = "desc";
            this.skip = 0;
            this.limit = Integer.MAX_VALUE;
            this.filterColumn = NameColumnsDB.MEASUREMENT_VALUE;
            this.dateEnd = "today";
            this.gt = -1;
            this.gte = -1;
            this.lt = -1;
            this.lte = -1;
        }

        /**
         * Sets the total of elements that should be returned in the search.
         * Default: Integer.MAX_VALUE
         *
         * @param limit int
         * @return Query
         */
        public Query limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Defines the type.
         * {@link HistoricalType}
         *
         * @param type int
         * @return Query
         */
        public Query type(int type) {
            if (!HistoricalType.isSupportedType(type))
                throw new IllegalArgumentException("type of indeterminate history!");

            this.type = type;
            return this;
        }

        /**
         * Defines the Params.
         *
         * @param params {@link Params}
         * @return Query
         */
        public Query params(Params params) {
            if (params == null) throw new NullPointerException("params == null");

            this.params = params;
            return this;
        }

        /**
         * Apply Sort Attributes.
         *
         * @param sort  String
         * @param order String
         * @return Query
         */
        public Query ordination(String sort, String order) {
            this.sort(sort);
            this.order(order);
            return this;
        }

        /**
         * Apply Pagination Attributes.
         *
         * @param skip  int
         * @param limit int
         * @return Query
         */
        public Query pagination(int skip, int limit) {
            this.skip(skip);
            this.limit(limit);
            return this;
        }

        /**
         * Apply filter Attributes.
         * operator1: =, <, <=, > or >=
         *
         * @param filterColumn String
         * @param opt          String
         * @param value        long
         * @return Query
         */
        public Query filterValue(String filterColumn, String opt, long value) {
            this.filterColumn(filterColumn);
            processOperators(opt, value);

            return this;
        }

        /**
         * Apply filter Attributes.
         * operator1: =, <, <=, > or >=
         * operator2: =, <, <=, > or >=
         *
         * @param filterColumn String
         * @param opt1         String - operator 1
         * @param value1       long - value 1
         * @param opt2         String - operator 2
         * @param value2       long - value 2
         * @return Query
         */
        public Query filterValue(String filterColumn, String opt1, long value1, String opt2, long value2) {
            this.filterColumn(filterColumn);
            processOperators(opt1, value1);
            processOperators(opt2, value2);
            return this;
        }

        /**
         * Apply Date Attributes.
         *
         * @param dateStart String
         * @param dateEnd   String
         * @return Query
         */
        public Query filterDate(String dateStart, @Nullable String dateEnd) {
            this.dateStart(dateStart);
            if (dateEnd != null) this.dateEnd(dateEnd);
            return this;
        }

        /**
         * Apply period Attributes.
         *
         * @param period String
         * @return Query
         */
        public Query filterDate(String period) {
            this.period(period);
            return this;
        }

        /**
         * Construct query strings.
         *
         * @return String
         */
        private String buildQueryStrings() {
            StringBuilder result = new StringBuilder();
            result.append("?");

            /**
             * Ordenation
             */
            result.append("sort=".concat(this.sort));
            result.append(SEPARATOR);
            result.append("order=".concat(this.order));

            /**
             * Pagination
             */
            result.append(SEPARATOR);
            result.append("skip=".concat(String.valueOf(this.skip)));
            result.append(SEPARATOR);
            result.append("limit=".concat(String.valueOf(this.limit)));

            /**
             * Comparations/filter
             */
            result.append(buildQueryStringsFilter());

            /**
             * Date
             */
            result.append(buildQueryStringsDate());

            return String.valueOf(result);
        }

        /**
         * Construct query strings filter.
         *
         * @return String
         */
        private String buildQueryStringsFilter() {
            StringBuilder result = new StringBuilder();

            if (this.gt > -1) {
                result.append(SEPARATOR);
                result.append("filterColumn=".concat(this.filterColumn));
                result.append(SEPARATOR);
                result.append("gt=".concat(String.valueOf(this.gt)));

                if (this.lt > -1) { // > value <
                    result.append(SEPARATOR);
                    result.append("lt=".concat(String.valueOf(this.lt)));
                } else if (this.lte > -1) { // > value =<
                    result.append(SEPARATOR);
                    result.append("lte=".concat(String.valueOf(this.lte)));
                }
            } else if (this.gte > -1) {
                result.append(SEPARATOR);
                result.append("filterColumn=".concat(this.filterColumn));
                result.append(SEPARATOR);
                result.append("gte=".concat(String.valueOf(this.gte)));

                if (this.lt > -1) { // > value <
                    result.append(SEPARATOR);
                    result.append("lt=".concat(String.valueOf(this.lt)));
                } else if (this.lte > -1) { // > value =<
                    result.append(SEPARATOR);
                    result.append("lte=".concat(String.valueOf(this.lte)));
                }
            } else if (this.lt > -1) { // <= value
                result.append(SEPARATOR);
                result.append("filterColumn=".concat(this.filterColumn));
                result.append(SEPARATOR);
                result.append("lt=".concat(String.valueOf(this.lt)));
            } else if (this.lte > -1) { // <= value
                result.append(SEPARATOR);
                result.append("filterColumn=".concat(this.filterColumn));
                result.append(SEPARATOR);
                result.append("lte=".concat(String.valueOf(this.lte)));
            }

            return String.valueOf(result);
        }

        /**
         * Construct query strings date.
         *
         * @return String
         */
        private String buildQueryStringsDate() {
            StringBuilder result = new StringBuilder();

            if (this.dateStart != null) {
                result.append(SEPARATOR);
                result.append("dateStart=".concat(this.dateStart));

                if (this.dateEnd != null) {
                    result.append(SEPARATOR);
                    result.append("dateEnd=".concat(this.dateEnd));
                }
            } else if (this.period != null && isValidPeriod(this.period)) {
                result.append(SEPARATOR);
                result.append("period=".concat(this.period));
            }

            return String.valueOf(result);
        }

        /**
         * Sets start date.
         * Date in American format: YYYY-MM-DD
         *
         * @param dateStart String
         * @return Query
         */
        private Query dateStart(String dateStart) {
            if (dateStart == null) throw new NullPointerException("dateStart == null");
            if (dateStart.length() == 0)
                throw new IllegalArgumentException("dateStart.length() == 0");
            if (!DateUtils.isDateValid(dateStart, null))
                throw new IllegalArgumentException("dataStart does not have the valid format; yyyy-MM-dd");

            this.dateStart = dateStart;
            return this;
        }

        /**
         * Sets the end date.
         * Date in American format: YYYY-MM-DD or today.
         * Default: today
         *
         * @param dateEnd String
         * @return Query
         */
        private Query dateEnd(String dateEnd) {
            if (dateEnd == null) throw new NullPointerException("dateEnd == null");
            if (dateEnd.length() == 0)
                throw new IllegalArgumentException("dateEnd.length() == 0");
            if (!DateUtils.isDateValid(dateEnd, null) && !dateEnd.equalsIgnoreCase("today"))
                throw new IllegalArgumentException("dateEnd does not have the valid format; yyyy-MM-dd or today");

            this.dateEnd = dateEnd;
            return this;
        }

        /**
         * Sets the starting position of the cursor in the search.
         * Default: 0
         *
         * @param skip int
         * @return Query
         */
        private Query skip(int skip) {
            this.skip = skip;
            return this;
        }

        /**
         * Sets the name of the column for sorting.
         *
         * @param sort String
         * @return Query
         */
        private Query sort(String sort) {
            if (sort == null) throw new NullPointerException("sort == null");
            if (sort.length() == 0) throw new IllegalArgumentException("sort.length() == 0");

            this.sort = sort;
            return this;
        }

        /**
         * Sets the ASC or DESC sort mode.
         *
         * @param order String
         * @return Query
         */
        private Query order(String order) {
            if (order == null) throw new NullPointerException("order == null");
            if (order.length() == 0) throw new IllegalArgumentException("order.length() == 0");
            if (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("ASC"))
                throw new IllegalArgumentException("order != ASC or DESC");

            this.order = order;

            return this;
        }

        /**
         * Defines which value of the "filterColumn" field is greater than (that is, >) the specified value.
         *
         * @param gt long
         * @return Query
         */
        private Query gt(long gt) {
            this.gt = gt;
            return this;
        }

        /**
         * Define which value of the field "filterColumn" is greater than or equal to (that is, >=) the specified value.
         *
         * @param gte long
         * @return Query
         */
        private Query gte(long gte) {
            this.gte = gte;
            return this;
        }

        /**
         * Defines the value of the "filterColumn" field is less than (that is, <) the specified value.
         *
         * @param lt long
         * @return Query
         */
        private Query lt(long lt) {
            this.lt = lt;
            return this;
        }

        /**
         * Defines which value of the "filterColumn" field is less than or equal to (that is, <=) the specified value.
         *
         * @param lte long
         * @return Query
         */
        private Query lte(long lte) {
            this.lte = lte;
            return this;
        }

        /**
         * Defines the name of the column used in the comparison.
         * Default: registrationDate
         *
         * @param filterColumn String
         * @return Query
         */
        private Query filterColumn(String filterColumn) {
            if (filterColumn == null) throw new NullPointerException("filterColumn == null");
            if (filterColumn.length() == 0)
                throw new IllegalArgumentException("filterColumn.length() == 0");

            this.filterColumn = filterColumn;
            return this;
        }

        /**
         * Defines the period.
         * [1..n]d - Total days
         * [1..n]w - Total weeks
         * [1..n]m - Total months
         * [1..n]y - Total years
         *
         * @param period String
         * @return Query
         */
        private Query period(String period) {
            if (period == null) throw new NullPointerException("period == null");
            if (period.length() == 0) throw new IllegalArgumentException("period.length() == 0");
            if (!isValidPeriod(period))
                throw new IllegalArgumentException("period does not have the valid format!");

            this.period = period;
            return this;
        }

        /**
         * Defines the urn.
         *
         * @param urn String
         * @return Query
         */
        private Query urn(String urn) {
            if (urn == null) throw new NullPointerException("urn == null");
            if (urn.length() == 0) throw new IllegalArgumentException("urn.length() == 0");

            this.urn = urn;
            return this;
        }

        /**
         * Process Operators.
         *
         * @param opt   String
         * @param value long
         */
        private void processOperators(String opt, long value) {
            if (opt.equals("=")) {
                this.gt(value - 1);
                this.lte(value);
            } else if (opt.equals(">")) {
                this.gt(value);
            } else if (opt.equals(">=")) {
                this.gte(value);
            } else if (opt.equals("<")) {
                this.lt(value);
            } else if (opt.equals("<=")) {
                this.lte(value);
            }
        }

        /**
         * Validate period.
         *
         * @param period String
         * @return boolean
         */
        private boolean isValidPeriod(String period) {
            if (period == null) return false;

            return period.matches("[0-9]{1,}[d||w|m|y]");
        }

        /**
         * Constructs the urn according to the type of historical and params.
         *
         * @return String
         */
        private String buildURN() {
            if (this.params == null)
                throw new IllegalArgumentException("params is required!");

            String result = null;
            if (type == HistoricalType.MEASUREMENTS_USER) {
                // /measurements/users/:userId
                String userId = this.params.userId();
                if (userId == null || userId.length() == 0)
                    throw new IllegalArgumentException("params deviceId is required!");

                result = "/measurements/users/:userId".replaceAll(":userId", userId);
            } else if (type == HistoricalType.MEASUREMENTS_DEVICE_USER) {
                // /measurements/devices/:deviceId/users/:userId
                String deviceId = this.params.deviceId();
                if (deviceId == null || deviceId.length() == 0)
                    throw new IllegalArgumentException("params deviceId is required!");

                String userId = this.params.userId();
                if (userId == null || userId.length() == 0)
                    throw new IllegalArgumentException("params userId is required!");

                result = "/measurements/devices/:deviceId/users/:userId"
                        .replaceAll(":deviceId", deviceId)
                        .replaceAll(":userId", userId);
            } else if (type == HistoricalType.MEASUREMENTS_TYPE_USER) {
                // /measurements/types/:typeId/users/:userId
                String typeId = String.valueOf(this.params.typeId());
                String userId = this.params.userId();
                if (userId == null || userId.length() == 0)
                    throw new IllegalArgumentException("params userId is required!");

                result = "/measurements/types/:typeId/users/:userId"
                        .replaceAll(":typeId", typeId)
                        .replaceAll(":userId", userId);
            } else if (type == HistoricalType.ELDERLIES_USER) {
                String userId = this.params.userId();
                if (userId == null || userId.length() == 0)
                    throw new IllegalArgumentException("params userId is required!");

                result = "/users/:userId/external".replaceAll(":userId", userId);
            } else {
                throw new IllegalArgumentException("type of indeterminate history!");
            }

            return result;
        }

        /**
         * Return historical
         *
         * @return
         */
        public Historical build() {
            this.urn(this.buildURN());
            this.queryStrings = buildQueryStrings();

            return new Historical(this);
        }


    }
}