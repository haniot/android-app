package br.edu.uepb.nutes.haniot.model.dao;

/**
 * Represents Measurement BodyCompositionMonitorDAO.
 *
 * @author Lucas barbosa <lucas.barbosa@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */

public class MeasurementBodyCompositionMonitorDAO {
    public static MeasurementBodyCompositionMonitorDAO instance;

    private MeasurementBodyCompositionMonitorDAO() {
    }

    public static synchronized MeasurementBodyCompositionMonitorDAO getInstance() {
        if (instance == null) instance = new MeasurementBodyCompositionMonitorDAO();

        return instance;
    }
//
//    @Override
//    public long save(MeasurementBodyCompositionMonitor o) {
//        if (o == null)
//            return -1;
//
//        return SugarRecord.save(o);
//    }
//
//    @Override
//    public long update(MeasurementBodyCompositionMonitor o) {
//        if (o == null)
//            return -1;
//
//        return SugarRecord.update(o);
//    }
//
//    @Override
//    public boolean remove(MeasurementBodyCompositionMonitor o) {
//        if (o == null)
//            return false;
//
//        return SugarRecord.delete(o);
//    }
//
//    @Override
//    public int removeAll(String deviceAddress, String userId) {
//        return SugarRecord.deleteAll(MeasurementScale.class, "DEVICE_ADDRESS = ? AND USER_ID = ?", deviceAddress, userId);
//    }
//
//    @Override
//    public List<MeasurementBodyCompositionMonitor> listAll(String deviceAddress, String userId) {
//        if (deviceAddress == null)
//            return new ArrayList<>();
//
//        return SugarRecord.find(MeasurementBodyCompositionMonitor.class, "DEVICE_ADDRESS = ? AND USER_ID = ?", deviceAddress, userId);
//    }
//
//    @Override
//    public List<MeasurementBodyCompositionMonitor> filter(long dateStart, long dateEnd, String deviceAddress, String userId) {
//        if (deviceAddress == null)
//            return new ArrayList<>();
//
//        return SugarRecord.find(MeasurementBodyCompositionMonitor.class,
//                "(registration_time >= ? AND registration_time <= ?) AND DEVICE_ADDRESS = ? AND USER_ID = ?",
//                new String[]{String.valueOf(dateStart), String.valueOf(dateEnd), deviceAddress, userId});
//    }
//
//    @Override
//    public List<MeasurementBodyCompositionMonitor> getNotSent(String deviceAddress, String userId) {
//        if (deviceAddress == null)
//            return new ArrayList<>();
//
//        return SugarRecord.find(MeasurementBodyCompositionMonitor.class, "HAS_SENT = 0 AND DEVICE_ADDRESS = ? AND USER_ID = ?", deviceAddress, userId);
//    }
//
//    @Override
//    public List<MeasurementBodyCompositionMonitor> getWasSent(String deviceAddress, String userId) {
//        if (deviceAddress == null)
//            return new ArrayList<>();
//
//        return SugarRecord.find(MeasurementBodyCompositionMonitor.class, "HAS_SENT = 1 AND DEVICE_ADDRESS = ? AND USER_ID = ?", deviceAddress, userId);
//    }
}
