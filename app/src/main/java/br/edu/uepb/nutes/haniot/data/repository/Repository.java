package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.data.model.model.Device;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;

public class Repository {

    private static Repository instance;

    public static Repository getInstance(Context context) {
        if (instance == null)
            instance = new Repository(context);
        return instance;
    }

    private Repository(Context context) {
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);

        this.deviceDAO = DeviceDAO.getInstance(context);
        this.feedingHabitsDAO = FeedingHabitsDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.medicalRecordDAO = MedicalRecordDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.physicalActivityHabitsDAO = PhysicalActivityHabitsDAO.getInstance(context);
        this.pilotStudyDAO = PilotStudyDAO.getInstance(context);
        this.sleepHabitsDAO = SleepHabitsDAO.getInstance(context);
        this.userDAO = UserDAO.getInstance(context);
    }

    private HaniotNetRepository haniotNetRepository; // Repository remote
    // repositories local
    private DeviceDAO deviceDAO;
    private FeedingHabitsDAO feedingHabitsDAO;
    private MeasurementDAO measurementDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private PatientDAO patientDAO;
    private PhysicalActivityHabitsDAO physicalActivityHabitsDAO;
    private PilotStudyDAO pilotStudyDAO;
    private SleepHabitsDAO sleepHabitsDAO;
    private UserDAO userDAO;


    /**
     * Adds a new device to the database.
     *
     * @param device Device
     * @return boolean
     */
    public boolean saveDevice(@NonNull Device device) {
        // convert device to device with annotation
        return deviceDAO.save(device);
    }


    /**
     * Select a Device according to id.
     *
     * @param id long
     * @return Object
     */
    public Device get(long id) {
        return deviceBox.query()
                .equal(Device_.id, id)
                .build().findFirst();
    }

    /**
     * Select a Device according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public Device get(@NonNull String address, String userId) {
        return deviceBox.query()
                .equal(Device_.address, address)
                .equal(Device_.userId, userId)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return Device
     */
    public Device getByType(@NonNull String userId, String type) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .equal(Device_.type, type)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId.
     *
     * @param userId long
     * @return List<T>
     */
    public List<Device> list(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().find();
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device Device
     * @return boolean
     */
    public boolean update(@NonNull Device device) {
        if (device.getId() == 0) {
            Device deviceUp = get(device.getId());

            // Id is required for an updateOrSave
            // Otherwise it will be an insert
            if (deviceUp == null) return false;

            device.setId(deviceUp.getId());
        }

        return save(device); // updateOrSave
    }

    /**
     * Removes device passed as parameter.
     *
     * @param device Device
     * @return boolean
     */
    public boolean remove(@NonNull Device device) {
        return (deviceBox.query()
                .equal(Device_.id, device.getId())
                .build()
                .remove()) > 0;
    }

    /**
     * Removes device passed as parameter.
     *
     * @param _id String
     * @return boolean
     */
    public boolean remove(@NonNull String _id) {
        return (deviceBox.query()
                .equal(Device_._id, _id)
                .build()
                .remove()) > 0;
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId long
     * @return long - Total number of itemsList removed
     */
    public boolean removeAll(@NonNull long userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().remove() > 0;
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAll(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_._id, userId)
                .build().remove() > 0;
    }
}
