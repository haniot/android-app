package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.service.autofill.UserData;

import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;

public class Repository {

    private static Repository instance;

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

    public static Repository getInstance(Context context) {
        if (instance == null)
            instance = new Repository(context);
        return instance;
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
}
