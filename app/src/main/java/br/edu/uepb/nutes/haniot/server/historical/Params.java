package br.edu.uepb.nutes.haniot.server.historical;

/**
 * Params used in historical query.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class Params {
    /**
     * user _id registered on the server.
     */
    private String userId;

    /**
     * device _id registered on the server.
     */
    private String deviceId;

    /**
     * Type of measurement.
     * {@link br.edu.uepb.nutes.haniot.data.model.MeasurementType}
     */
    private int typeId;

    public Params(String userId, String deviceId, int typeId) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.typeId = typeId;
    }

    public Params(String userId, int typeId) {
        this.userId = userId;
        this.typeId = typeId;
    }

    public Params(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the userId.
     *
     * @param userId String
     * @return Query
     */
    public void userId(String userId) {
        if (userId == null) throw new NullPointerException("userId == null");
        if (userId.length() == 0) throw new IllegalArgumentException("userId.length() == 0");

        this.userId = userId;
    }

    /**
     * Sets the deviceId.
     *
     * @param deviceId String
     * @return Query
     */
    public void deviceId(String deviceId) {
        if (deviceId == null) throw new NullPointerException("deviceId == null");
        if (deviceId.length() == 0) throw new IllegalArgumentException("deviceId.length() == 0");

        this.deviceId = deviceId;
    }

    /**
     * Sets the typeId representing the type of measurement.
     *
     * @param typeId String
     * @return Query
     */
    public void typeId(int typeId) {
//        if (MeasurementType.isSupportedType(typeId))
//            throw new IllegalArgumentException("measurement type indeterminate!");

        this.typeId = typeId;
    }

    public String userId() {
        return userId;
    }

    public String deviceId() {
        return deviceId;
    }

    public int typeId() {
        return typeId;
    }

    @Override
    public String toString() {
        return "Params{" +
                "userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
