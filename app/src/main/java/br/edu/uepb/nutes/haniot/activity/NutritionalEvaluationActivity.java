package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.EvaluationAdapter;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_EMPTY_REQUIRED;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_ERROR;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ALL_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.BLOOD_PRESSURE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.GLUCOSE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.HEARTRATE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.HEIGHT;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.WAIST_CIRCUMFERENCE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.WEIGHT;

public class NutritionalEvaluationActivity extends AppCompatActivity implements EvaluationAdapter.OnClick {

    @BindView(R.id.list_evaluation)
    RecyclerView evaluation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gender_icon)
    ImageView gender;
    @BindView(R.id.send_evaluation)
    FloatingActionButton sendEvaluation;
    @BindView(R.id.message_patient)
    TextView messagePatient;
    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;
    @BindView(R.id.message_error)
    TextView messageError;

    EvaluationAdapter evaluationAdapter;
    private List<GroupItemEvaluation> groupItemEvaluations;
    private AppPreferencesHelper helper;
    private Patient patient;
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private NutritionalEvaluation nutritionalEvaluation;
    private PilotStudy pilotStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        initResources();

        nutritionalEvaluation = new NutritionalEvaluation();
        initToolbar();
        showMessage(-1);
        sendEvaluation.setOnClickListener(v -> {
            sendEvaluation();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareItems();
    }

    /**
     * Initialize NutritionalEvaluation recyclerview.
     */
    private void initRecyclerView() {
        evaluationAdapter = new EvaluationAdapter(groupItemEvaluations, this);
        evaluation.setLayoutManager(new LinearLayoutManager(this));
        evaluationAdapter.setListener(this);
        evaluation.setAdapter(evaluationAdapter);
        evaluationAdapter.expandAll();
    }

    /**
     * Initialize toolbar.
     */
    private void initToolbar() {
        toolbar.setTitle("Gerar Avaliação Nutricional");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messagePatient.setText(String.format(getResources().getString(R.string.evaluation_message),
                patient.getName(), "Nutricional"));
        if (patient.getGender().equals("female")) gender.setImageResource(R.drawable.x_girl);
        else gender.setImageResource(R.drawable.x_boy);
    }

    /**
     * Initialize resources.
     */
    private void initResources() {
        groupItemEvaluations = new ArrayList<>();
        helper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        patient = helper.getLastPatient();
        pilotStudy = helper.getLastPilotStudy();
    }

    private void showToast(final String menssage) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
            toast.show();
        });
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    public void showMessage(@StringRes int str) {
        if (str == -1) {
            boxMessage.setVisibility(View.GONE);
            return;
        }

        String message = getResources().getString(str);
        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Get get evaluation group object by type.
     *
     * @param type
     * @return
     */
    private GroupItemEvaluation getEvaluationGroupByType(int type) {
        for (GroupItemEvaluation groupItemEvaluation : groupItemEvaluations) {
            if (groupItemEvaluation.getType() == type) {
                return groupItemEvaluation;
            }
        }
        return null;
    }

    /**
     * Show recyclerview items in prepareItems mode.
     */
    private void prepareItems() {
        groupItemEvaluations.clear();
        List<ItemEvaluation> itemsLoading;

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xweight, TYPE_LOADING,
                getString(R.string.weight), WEIGHT));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.weight), itemsLoading, WEIGHT));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xglucosemeter, TYPE_LOADING,
                getString(R.string.glucose), GLUCOSE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.glucose), itemsLoading, GLUCOSE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.ic_height, TYPE_LOADING,
                getString(R.string.height), HEIGHT));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.height), itemsLoading, HEIGHT));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.ic_waist, TYPE_LOADING,
                getString(R.string.waits_circumference), WAIST_CIRCUMFERENCE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.waits_circumference),
                itemsLoading, WAIST_CIRCUMFERENCE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xcardiogram, TYPE_LOADING,
                getString(R.string.heart_rate), HEARTRATE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.heart_rate),
                itemsLoading, HEARTRATE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_LOADING,
                getString(R.string.blood_pressure), BLOOD_PRESSURE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure),
                itemsLoading, BLOOD_PRESSURE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Histórico de Saúde",
                itemsLoading, MEDICAL_RECORDS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Físicos",
                itemsLoading, PHYSICAL_ACTIVITY));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos Alimentares",
                itemsLoading, FEEDING_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Questionário Nutricional - Hábitos do Sono",
                itemsLoading, SLEEP_HABITS));

        initRecyclerView();
        downloadData();

    }

    /**
     * Prepare evaluation data from the server.
     *
     * @param data
     * @param type
     */
    private void prepareData(List data, int type) {
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
        int reminderFirst5 = 0;
        int reminderFirst1 = 0;
        int reminderFirst2 = 0;
        int reminderFirst3 = 0;
        int reminderFirst4 = 0;

        if (groupItemEvaluation == null) return;

        if (data.isEmpty() && !groupItemEvaluation.getItems().isEmpty())
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_EMPTY_REQUIRED);
        else {
            ItemEvaluation itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            groupItemEvaluation.getItems().clear();
            switch (type) {
                case SLEEP_HABITS:
                    for (SleepHabit sleepHabit : (List<SleepHabit>) data) {
                        if (reminderFirst1 == 0) itemEvaluation.setChecked(true);
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setSleepHabit(sleepHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                        reminderFirst1++;
                    }
                    break;
                case MEDICAL_RECORDS:
                    for (MedicalRecord medicalRecord : (List<MedicalRecord>) data) {
                        if (reminderFirst2 == 0) itemEvaluation.setChecked(true);
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setMedicalRecord(medicalRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                        reminderFirst2++;
                    }
                    break;
                case FEEDING_HABITS:
                    for (FeedingHabitsRecord feedingHabitsRecord : (List<FeedingHabitsRecord>) data) {
                        if (reminderFirst3 == 0) itemEvaluation.setChecked(true);
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setFeedingHabitsRecord(feedingHabitsRecord);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                        reminderFirst3++;
                    }
                    break;
                case PHYSICAL_ACTIVITY:
                    for (PhysicalActivityHabit physicalActivityHabit : (List<PhysicalActivityHabit>) data) {
                        if (reminderFirst4 == 0) itemEvaluation.setChecked(true);
                        itemEvaluation.setChecked(true);
                        itemEvaluation.setTypeHeader(TYPE_QUIZ);
                        itemEvaluation.setPhysicalActivityHabit(physicalActivityHabit);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                        reminderFirst4++;
                    }
                    break;
                default:
                    for (Measurement measurement : (List<Measurement>) data) {
                        if (reminderFirst5 == 0) itemEvaluation.setChecked(true);
                        itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
                        itemEvaluation.setMeasurement(measurement);
                        groupItemEvaluation.getItems().add(itemEvaluation);
                        itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
                        reminderFirst5++;
                    }
            }
        }
        evaluationAdapter.notifyDataSetChanged();
    }

    /**
     * Show error.
     *
     * @param
     */
    private void onDownloadError(int type) {
        if (type == ALL_MEASUREMENT) {
            getEvaluationGroupByType(WEIGHT).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(GLUCOSE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(WAIST_CIRCUMFERENCE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(HEIGHT).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(HEARTRATE).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(BLOOD_PRESSURE).getItems().get(0).setTypeHeader(TYPE_ERROR);
        } else {
            GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
            if (groupItemEvaluation == null) return;
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_ERROR);
        }
    }

    /**
     * Download data evaluation from the server.
     */
    private void downloadData() {

        DisposableManager.add(haniotNetRepository
                .getAllMedicalRecord(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(medicalRecords ->
                                prepareData(medicalRecords, MEDICAL_RECORDS),
                        error -> {
                            Log.i("AAA", error.getMessage());
                            onDownloadError(MEDICAL_RECORDS);
                        }));

        DisposableManager.add(haniotNetRepository
                .getAllPhysicalActivity(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(physicalActivityHabits ->
                                prepareData(physicalActivityHabits, PHYSICAL_ACTIVITY),
                        type -> onDownloadError(PHYSICAL_ACTIVITY)));

        DisposableManager.add(haniotNetRepository
                .getAllFeedingHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(feedingHabitsRecords ->
                                prepareData(feedingHabitsRecords, FEEDING_HABITS),
                        type -> onDownloadError(FEEDING_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllSleepHabits(helper.getLastPatient().get_id()
                        , 1, 20, "created_at")
                .subscribe(sleepHabits ->
                                prepareData(sleepHabits, SLEEP_HABITS),
                        type -> onDownloadError(SLEEP_HABITS)));

        DisposableManager.add(haniotNetRepository
                .getAllMeasurements(helper.getLastPatient().get_id()
                        , 1, 100000, "-timestamp")
                .subscribe(this::prepareMeasurements,
                        type -> onDownloadError(ALL_MEASUREMENT)));
//        DisposableManager.add(haniotNetRepository
//                .getAllMeasurements(helper.getLastPatient().get_id()
//                        , "created_at", pilotStudy.getStart(), pilotStudy.getEnd(), 1, 1000)
//                .subscribe(this::prepareMeasurements,
//                        type -> onDownloadError(ALL_MEASUREMENT)));
    }

    /**
     * Prepare measurements list from server.
     *
     * @param measurements
     */
    private void prepareMeasurements(List<Measurement> measurements) {
        List<Measurement> glucose = new ArrayList<>();
        List<Measurement> bloodPressure = new ArrayList<>();
        List<Measurement> heartRate = new ArrayList<>();
        List<Measurement> height = new ArrayList<>();
        List<Measurement> waistCircumference = new ArrayList<>();
        List<Measurement> weight = new ArrayList<>();

        int countHeartRate = 0;
        for (Measurement measurement : measurements) {
            Log.i("AAA", measurement.getValue() + " - " + measurement.getType());
            switch (measurement.getType()) {
                case "blood_glucose":
                    glucose.add(measurement);
                    break;
                case "blood_pressure":
                    bloodPressure.add(measurement);
                    break;
                case "heart_rate":
                    if (countHeartRate >= 5) break;
                    heartRate.add(measurement);
                    countHeartRate++;
                    break;
                case "height":
                    height.add(measurement);
                    break;
                case "waist_circumference":
                    waistCircumference.add(measurement);
                    break;
                case "weight":
                    weight.add(measurement);
                    break;
            }
        }

        prepareData(heartRate, HEARTRATE);
        prepareData(bloodPressure, BLOOD_PRESSURE);
        prepareData(weight, WEIGHT);
        prepareData(glucose, GLUCOSE);
        prepareData(waistCircumference, WAIST_CIRCUMFERENCE);
        prepareData(height, HEIGHT);
    }

    /**
     * Send nutritionalEvaluation for server.
     */
    private void sendEvaluation() {

        Log.i("AAA", "Preparing evaluation...");
        nutritionalEvaluation.setPatient(patient);
        nutritionalEvaluation.setHealthProfessionalId(appPreferencesHelper.getUserLogged().get_id());
        nutritionalEvaluation.setPilotStudy(appPreferencesHelper.getLastPilotStudy().get_id());
        if (nutritionalEvaluation.getMeasurements() != null)
            nutritionalEvaluation.getMeasurements().clear();

        if (getCheckedMeasurement(HEARTRATE)
                && getCheckedMeasurement(GLUCOSE)
                && getCheckedMeasurement(BLOOD_PRESSURE)
                && getCheckedMeasurement(WEIGHT)
                && getCheckedMeasurement(WAIST_CIRCUMFERENCE)
                && getCheckedMeasurement(HEIGHT)
                && getCheckedQuiz(PHYSICAL_ACTIVITY)
                && getCheckedQuiz(MEDICAL_RECORDS)
                && getCheckedQuiz(SLEEP_HABITS)
                && getCheckedQuiz(FEEDING_HABITS)) {

            Log.i("AAA", "Saida: " + nutritionalEvaluation.toJson());

            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_save_evaluation))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes_text), (dialog, id) -> {
                        DisposableManager.add(haniotNetRepository
                                .saveNutritionalEvaluation(nutritionalEvaluation)
                                .subscribe(nutritionalEvaluationResult -> {
                                    Toast.makeText(this, R.string.evaluation_sucessfull, Toast.LENGTH_LONG).show();
                                    finish();
                                }, this::errorHandler));
                    })
                    .setNegativeButton(getString(R.string.no_text), null)
                    .show();
        } else {
            showMessage(R.string.evaluation_required_fields);
        }
    }

    private boolean getCheckedQuiz(int type) {
        boolean contains = false;
        if (getEvaluationGroupByType(type) == null) return false;
        for (ItemEvaluation itemEvaluation : getEvaluationGroupByType(type).getItems())
            if (itemEvaluation.isChecked()) {
                switch (itemEvaluation.getTypeEvaluation()) {
                    case PHYSICAL_ACTIVITY:
                        nutritionalEvaluation.setPhysicalActivityHabits(itemEvaluation.getPhysicalActivityHabit());
                        contains = true;
                        break;
                    case SLEEP_HABITS:
                        nutritionalEvaluation.setSleepHabits(itemEvaluation.getSleepHabit());
                        contains = true;
                        break;
                    case MEDICAL_RECORDS:
                        nutritionalEvaluation.setMedicalRecord(itemEvaluation.getMedicalRecord());
                        contains = true;
                        break;
                    case FEEDING_HABITS:
                        nutritionalEvaluation.setFeedingHabits(itemEvaluation.getFeedingHabitsRecord());
                        contains = true;
                        break;
                }
            }
        return contains;
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        if (!checkConnectivity())
            showMessage(R.string.no_internet_conection);
        else
            showMessage(R.string.error_500);
    }

    /**
     * Check if you have connectivity.
     * If it does not, the elements in the view mounted to notify the user.
     *
     * @return boolean
     */
    private boolean checkConnectivity() {
        if (!ConnectionUtils.internetIsEnabled(this)) {
            return false;
        }
        return true;
    }

    /**
     * Verify if exists measurement by type selected.
     *
     * @param type
     * @return
     */
    private boolean getCheckedMeasurement(int type) {
        int count = 0;
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
        if (groupItemEvaluation == null) return false;

        for (ItemEvaluation itemEvaluation1 : groupItemEvaluation.getItems()) {
            if (itemEvaluation1.isChecked()) {
                nutritionalEvaluation.addMeasuerement(itemEvaluation1.getMeasurement());
                count++;
            }
        }

        return count > 0;
    }

    @Override
    public void onAddItemClick(String name, int type) {
        Intent intent;
        switch (type) {
            case GLUCOSE:
                intent = new Intent(this, GlucoseActivity.class);
                break;
            case HEARTRATE:
                intent = new Intent(this, HeartRateActivity.class);
                break;
            case WEIGHT:
                intent = new Intent(this, ScaleActivity.class);
                break;
            case HEIGHT:
            case WAIST_CIRCUMFERENCE:
                intent = new Intent(this, AddMeasurementActivity.class);
                AppPreferencesHelper.getInstance(this)
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.ANTHROPOMETRIC);
                break;
            case MEDICAL_RECORDS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", MEDICAL_RECORDS);
                break;
            case PHYSICAL_ACTIVITY:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", PHYSICAL_ACTIVITY);
                break;
            case FEEDING_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", FEEDING_HABITS);
                break;
            case SLEEP_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", SLEEP_HABITS);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation, boolean selected) {
        Log.i("AAA", "Antes:" + nutritionalEvaluation.toString());
        if (itemEvaluation.getTypeHeader() == TYPE_MEASUREMENT) {
            if (selected) nutritionalEvaluation.addMeasuerement(itemEvaluation.getMeasurement());
            else nutritionalEvaluation.removeMeasuerement(itemEvaluation.getMeasurement());
        }
        switch (itemEvaluation.getTypeEvaluation()) {
            case SLEEP_HABITS:
                if (selected) nutritionalEvaluation.setSleepHabits(itemEvaluation.getSleepHabit());
                else nutritionalEvaluation.setSleepHabits(null);
                break;
            case MEDICAL_RECORDS:
                if (selected)
                    nutritionalEvaluation.setMedicalRecord(itemEvaluation.getMedicalRecord());
                else nutritionalEvaluation.setMedicalRecord(null);
                break;
            case PHYSICAL_ACTIVITY:
                if (selected)
                    nutritionalEvaluation.setPhysicalActivityHabits(itemEvaluation.getPhysicalActivityHabit());
                else nutritionalEvaluation.setPhysicalActivityHabits(null);
                break;
            case FEEDING_HABITS:
                if (selected)
                    nutritionalEvaluation.setFeedingHabits(itemEvaluation.getFeedingHabitsRecord());
                else nutritionalEvaluation.setFeedingHabits(null);
                break;
            default:
                break;
        }
        Log.i("AAA", "Depois:" + nutritionalEvaluation.toString());
    }
}