package br.edu.uepb.nutes.haniot.server.historical;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.NameColumnsDB;

/**
 * Module to perform measurements queries on the server.
 *
 * @param <E>
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class Historical<E> {
    private final String sort;
    private final String order;
    private final int skip;
    private final int limit;
    private final String filterColumn;
    private final long gt;
    private final long gte;
    private final long lt;
    private final long lte;
    private final String dateStart;
    private final String dateEnd;
    private final String period;
    private final String urn;
    private final int type;
    private final Params params;
    private final Callback callback;
    private final Context context;

    private Historical(Query query) {
        this.sort = query.sort;
        this.order = query.order;
        this.skip = query.skip;
        this.limit = query.limit;
        this.filterColumn = query.filterColumn;
        this.gt = query.gt;
        this.gte = query.gte;
        this.lt = query.lt;
        this.lte = query.lte;
        this.dateStart = query.dateStart;
        this.dateEnd = query.dateEnd;
        this.period = query.period;
        this.urn = query.urn;
        this.type = query.type;
        this.params = query.params;
        this.callback = query.callback;
        this.context = query.context;

        requestToServer();
    }

    private void requestToServer() {
        if (this.callback == null) throw new IllegalArgumentException("callback is required!");
        if (this.context == null) throw new IllegalArgumentException("context is required!");

        this.callback.onBeforeSend();

        Log.w("URN ============== ", urn);
        Server.getInstance(context).get(this.urn, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                callback.onAfterSend();

                callback.onError(null);
            }

            @Override
            public void onSuccess(JSONObject result) {
                callback.onAfterSend();

                callback.onSuccess(buildObjects(result));
            }
        });
    }

    private List<Historical<E>> buildObjects(JSONObject data) {

        return null;
    }

    public static class Query {
        String sort;
        String order;
        int skip;
        int limit;
        String filterColumn;
        long gt;
        long gte;
        long lt;
        long lte;
        String dateStart;
        String dateEnd;
        String period;
        String urn;
        int type;
        Params params;
        Callback callback;
        Context context;

        public Query() {
            this.sort = NameColumnsDB.MEASUREMENT_REGISTRATION_DATE;
            this.order = "desc";
            this.skip = 0;
            this.limit = Integer.MAX_VALUE;
            this.filterColumn = NameColumnsDB.MEASUREMENT_REGISTRATION_DATE;
            this.dateEnd = "today";
        }

        /**
         * Sets the name of the column for sorting.
         *
         * @param sort String
         * @return Query
         */
        public Query sort(String sort) {
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
        public Query order(String order) {
            if (order == null) throw new NullPointerException("order == null");
            if (order.length() == 0) throw new IllegalArgumentException("order.length() == 0");
            if (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("ASC"))
                throw new IllegalArgumentException("order != ASC or DESC");

            this.order = order;

            return this;
        }

        /**
         * Sets the starting position of the cursor in the search.
         * Default: 0
         *
         * @param skip int
         * @return Query
         */
        public Query skip(int skip) {
            this.skip = skip;
            return this;
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
         * Defines which value of the "filterColumn" field is greater than (that is, >) the specified value.
         *
         * @param gt long
         * @return Query
         */
        public Query gt(long gt) {
            this.gt = gt;
            return this;
        }

        /**
         * Define which value of the field "filterColumn" is greater than or equal to (that is, >=) the specified value.
         *
         * @param gte long
         * @return Query
         */
        public Query gte(long gte) {
            this.gte = gte;
            return this;
        }

        /**
         * Defines the value of the "filterColumn" field is less than (that is, <) the specified value.
         *
         * @param lt long
         * @return Query
         */
        public Query lt(long lt) {
            this.lt = lt;
            return this;
        }

        /**
         * Defines which value of the "filterColumn" field is less than or equal to (that is, <=) the specified value.
         *
         * @param lte long
         * @return Query
         */
        public Query lte(long lte) {
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
        public Query filterColumn(String filterColumn) {
            if (filterColumn == null) throw new NullPointerException("filterColumn == null");
            if (filterColumn.length() == 0)
                throw new IllegalArgumentException("filterColumn.length() == 0");

            this.filterColumn = filterColumn;
            return this;
        }

        /**
         * Sets start date.
         * Date in American format: YYYY-MM-DD
         *
         * @param dateStart String
         * @return Query
         */
        public Query dateStart(String dateStart) {
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
        public Query dateEnd(String dateEnd) {
            if (dateEnd == null) throw new NullPointerException("dateEnd == null");
            if (dateEnd.length() == 0)
                throw new IllegalArgumentException("dateEnd.length() == 0");
            if (!DateUtils.isDateValid(dateEnd, null) && !dateEnd.equalsIgnoreCase("today"))
                throw new IllegalArgumentException("dateEnd does not have the valid format; yyyy-MM-dd or today");

            this.dateEnd = dateEnd;
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
        public Query period(String period) {
            if (period == null) throw new NullPointerException("period == null");
            if (period.length() == 0) throw new IllegalArgumentException("period.length() == 0");
            if (period.equalsIgnoreCase("today"))
                throw new IllegalArgumentException("period does not have the valid format; yyyy-MM-dd or today");

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
         * Defines the type.
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
         * Defines the context.
         *
         * @param context Context
         * @return Query
         */
        public Query context(Context context) {
            if (context == null) throw new NullPointerException("context == null");

            this.context = context;
            return this;
        }

        public Query callback(Callback callback) {
            this.callback = callback;
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
         * Apply Date Attributes.
         *
         * @param dateStart String
         * @param dateEnd   String
         * @return Query
         */
        public Query date(String dateStart, String dateEnd) {
            this.dateStart(dateStart);
            this.dateEnd(dateEnd);
            return this;
        }

        /**
         * Apply period Attributes.
         *
         * @param dateStart String
         * @param dateEnd   String
         * @return Query
         */
        public Query datePeriod(String dateStart, String dateEnd) {
            this.dateStart(dateStart);
            this.dateEnd(dateEnd);
            return this;
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
         * Return historical
         *
         * @return
         */
        public Historical build() {
            this.urn(this.buildURN());

            return new Historical(this);
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
            } else {
                throw new IllegalArgumentException("type of indeterminate history!");
            }

            return result;
        }
    }

    /**
     * Callback to return from the remote server
     */
    public interface Callback<E> {
        void onError(@Nullable E result);

        void onSuccess(E result);

        void onBeforeSend();

        void onAfterSend();
    }
}

