package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
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
import br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;

/**
 * Classe responsável por gerenciar o repositório local e o repositório remoto
 */
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

    // ------------- DEVICE DAO --------------------------

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean saveDevice(@NonNull Device device) {
        return deviceDAO.save(Convert.convertDevice(device));
    }


    /**
     * Select a DeviceOB according to id.
     *
     * @param id long
     * @return Object
     */
    public Device getDevice(long id) {
        return Convert.convertDevice(deviceDAO.get(id));
    }

    /**
     * Select a DeviceOB according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public Device getDevice(@NonNull String address, String userId) {
        return Convert.convertDevice(deviceDAO.get(address, userId));
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return DeviceOB
     */
    public Device getDeviceByType(@NonNull String userId, String type) {
        return Convert.convertDevice(deviceDAO.getByType(userId, type));
    }

    /**
     * Retrieves all device according to userId.
     *
     * @param userId long
     * @return List<T>
     */
    public List<Device> listDevices(@NonNull String userId) {
        return Convert.listDeviceToModel(deviceDAO.list(userId));
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean updateDevice(@NonNull Device device) {
        return deviceDAO.update(Convert.convertDevice(device));
    }

    /**
     * Removes device passed as parameter.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean removeDevice(@NonNull Device device) {
        return deviceDAO.remove(Convert.convertDevice(device));
    }

    /**
     * Removes device passed as parameter.
     *
     * @param _id String
     * @return boolean
     */
    public boolean removeDevice(@NonNull String _id) {
        return deviceDAO.remove(_id);
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId long
     * @return long - Total number of itemsList removed
     */
    public boolean removeAllDevices(@NonNull long userId) {
        return deviceDAO.removeAll(userId);
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAllDevices(@NonNull String userId) {
        return deviceDAO.removeAll(userId);
    }

    // ------------- Feeding Habits DAO ---------------------


    //    Search FeedingHabitsRecordOB by id
    public FeedingHabitsRecord getFeedingHabitsRecordFromPatientId(@NonNull String _id) {
        return Convert.convertFeedingHabitsRecord(feedingHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<FeedingHabitsRecord> getFeedingHabitsRecord() {
        return Convert.listFeedingHabitsRecordToModel(feedingHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.save(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.update(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.remove(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }


    // ----------- MEASUREMENT DAO -----------------------

    /**
     * Adds a new measurement to the database.
     *
     * @param measurement
     * @return boolean
     */
    public boolean saveMeasurement(@NonNull Measurement measurement) {
        return measurementDAO.save(Convert.convertMeasurement(measurement));
    }

    /**
     * Update measurement data.
     *
     * @param measurement
     * @return boolean
     */
    public boolean updateMeasurement(@NonNull Measurement measurement) {
        return measurementDAO.update(Convert.convertMeasurement(measurement));
    }

    /**
     * Remove measurement.
     *
     * @param measurement
     * @return boolean
     */
    public boolean removeMeasurement(@NonNull Measurement measurement) {
        return measurementDAO.remove(Convert.convertMeasurement(measurement));
    }

    /**
     * Remove all measurements associated with device and user.
     *
     * @param deviceId long
     * @param userId   long
     * @return boolean
     */
    public boolean removeAllMeasurements(@NonNull long deviceId, @NonNull long userId) {
        return measurementDAO.removeAll(deviceId, userId);
    }

    /**
     * Remove all measurements associated with user.
     *
     * @param userId long
     * @return boolean
     */
    public boolean removeAllMeasurements(@NonNull String userId) {
        return measurementDAO.removeAll(userId);
    }

    /**
     * Select a measurement.
     *
     * @param id long
     * @return Object
     */
    public Measurement getMeasurement(@NonNull long id) {
        return Convert.convertMeasurement(measurementDAO.get(id));
    }

    /**
     * Select all measurements associated with the user.
     *
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<Measurement> listMeasurements(@NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return Convert.listMeasurementsToModel(measurementDAO.list(userId, offset, limit));
    }

    /**
     * Select all measurements of a type associated with the user.
     *
     * @param type   {@link String}
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<Measurement> listMeasurements(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return Convert.listMeasurementsToModel(measurementDAO.list(type, userId, offset, limit));
    }

    /**
     * Select all measurements associated with the device and user.
     *
     * @param deviceId long
     * @param userId   long
     * @param offset   int
     * @param limit    int
     * @return List<MeasurementOB>
     */
    public List<Measurement> listMeasurements(@NonNull long deviceId, @NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return Convert.listMeasurementsToModel(measurementDAO.list(deviceId, userId, offset, limit));
    }

    /**
     * Select all measurements of a user that were not sent to the remote server.
     *
     * @param userId long
     * @return List<MeasurementOB>
     */
    public List<Measurement> getNotSentMeasurement(@NonNull long userId) {
        return Convert.listMeasurementsToModel(measurementDAO.getNotSent(userId));
    }

    // ---------- MEDICAL RECORD DAO ----------------

    //    Search FeedingHabitsRecordOB by id
    public MedicalRecord getMedicalRecordFromPatientId(@NonNull String _id) {
        return Convert.convertMedicalRecord(medicalRecordDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<MedicalRecord> getMedicalRecord() {
        return Convert.listMedicalRecordsToModel(medicalRecordDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.save(Convert.convertMedicalRecord(medicalRecord));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.update(Convert.convertMedicalRecord(medicalRecord));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.remove(Convert.convertMedicalRecord(medicalRecord));
    }

    // --------- PATIENT DAO --------------

    public Patient getPatient(long id) {
        return Convert.convertPatient(patientDAO.get(id));
    }

    public Patient getPatient(@NonNull String _id) {
        return Convert.convertPatient(patientDAO.get(_id));
    }

    public List<Patient> listPatients(@NonNull String healthProfessionalId) {
        return Convert.listPatientsToModel(patientDAO.list(healthProfessionalId));
    }

    public boolean savePatient(@NonNull Patient patient) {
        return patientDAO.save(Convert.convertPatient(patient));
    }

    public boolean updatePatient(@NonNull Patient patient) {
        return patientDAO.update(Convert.convertPatient(patient));
    }

    public boolean removePatient(@NonNull Patient patient) {
        return patientDAO.remove(Convert.convertPatient(patient));
    }

    public boolean removePatient(@NonNull String _id) {
        return patientDAO.remove(_id);
    }

    // ------------ PHYSICAL ACTIVITY HABITS DAO -----------


    //    Search FeedingHabitsRecordOB by id
    public PhysicalActivityHabit getPhysicalActivityHabitFromPatientId(@NonNull String _id) {
        return Convert.convertPhysicalActivityHabit(physicalActivityHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<PhysicalActivityHabit> getPhysicalActivityHabits() {
        return Convert.convertListPhysicalActivityHabit(physicalActivityHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean savePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.save(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    //    update FeedingHabitsRecordOB
    public boolean updatePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.update(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.remove(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    // ----------------- PILOT STUDY DAO -------------------------

    public PilotStudy getPilotStudy(long id) {
        return Convert.convertPilotStudy(pilotStudyDAO.get(id));
    }

    public PilotStudy getPilotStudy(@NonNull String _id) {
        return Convert.convertPilotStudy(pilotStudyDAO.get(_id));
    }

    public List<PilotStudy> listPilotStudies(String userId) {
        return Convert.listPilotStudiesToModel(pilotStudyDAO.list(userId));
    }

    public boolean savePilotStudy(@NonNull PilotStudy pilotStudy) {
        return pilotStudyDAO.save(Convert.convertPilotStudy(pilotStudy));
    }

    public boolean updatePilotStudy(@NonNull PilotStudy pilotStudy) {
        return pilotStudyDAO.update(Convert.convertPilotStudy(pilotStudy));
    }

    public void clearSelectedPilotStudy(@NonNull String userId) {
        pilotStudyDAO.clearSelected(userId);
    }

    public boolean removePilotStudy(@NonNull PilotStudy pilotStudy) {
        return pilotStudyDAO.remove(Convert.convertPilotStudy(pilotStudy));
    }

    public boolean removePilotStudy(@NonNull String _id) {
        return pilotStudyDAO.remove(_id);
    }

    public boolean removeAllPilotStudiesy(@NonNull String userId) {
        return pilotStudyDAO.removeAll(userId);
    }

    // --------------- SLEEP HABITS DAO --------------------

    //    Search FeedingHabitsRecordOB by id
    public SleepHabit getSleepHabitsFromPatientId(@NonNull String _id) {
        return Convert.convertSleepHabit(sleepHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<SleepHabit> getSleepHabits() {
        return Convert.listSleepHabitsToModel(sleepHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.save(Convert.convertSleepHabit(sleepHabit));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.update(Convert.convertSleepHabit(sleepHabit));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.remove(Convert.convertSleepHabit(sleepHabit));
    }

    // ---------- USER DAO ---------------

    /**
     * get user for _id.
     *
     * @param _id String
     * @return UserOB
     */
    public User getUser(@NonNull String _id) {
        return Convert.convertUser(userDAO.get(_id));
    }

    /**
     * Selects user based on local id
     *
     * @param id long
     * @return UserOB
     */
    public User getUser(@NonNull long id) {
        return Convert.convertUser(userDAO.get(id));
    }

    public List<User> listAllUsers() {
        return null;
    }

    /**
     * Add new user.
     *
     * @param user
     * @return boolean
     */
    public boolean saveUser(@NonNull User user) {
        return userDAO.save(Convert.convertUser(user));
    }

    /**
     * Update user.
     *
     * @param user
     * @return boolean
     */
    public boolean updateUser(@NonNull User user) {
        return userDAO.update(Convert.convertUser(user));
    }

    /**
     * Remove user.
     *
     * @param id
     * @return boolean
     */
    public boolean removeUser(@NonNull long id) {
        return userDAO.remove(id);
    }

    /**
     * Remove user.
     *
     * @param id
     * @return boolean
     */
    public boolean removeUser(@NonNull String id) {
        return userDAO.remove(id);
    }

}
