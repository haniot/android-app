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
import java.util.HashMap;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.EvaluationAdapter;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_EMPTY_REQUIRED;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_ERROR;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ALL_MEASUREMENT;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ALL_QUIZ;
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
    private NutritionalQuestionnaire lastNutritionalQuestionnaire;
    private List<Measurement> measurementList;
    private boolean leftFields;
    private MeasurementLastResponse measurementLastResponse;
    private HashMap<Integer, Boolean> validated;
    private User user;

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
        validated = new HashMap<>();
        groupItemEvaluations = new ArrayList<>();
        helper = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        patient = helper.getLastPatient();
        user = helper.getUserLogged();
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
     * Get get listNutritional group object by type.
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
        itemsLoading.add(new ItemEvaluation(R.drawable.xblood_pressure, TYPE_LOADING,
                getString(R.string.blood_pressure), BLOOD_PRESSURE));
        groupItemEvaluations.add(new GroupItemEvaluation(getString(R.string.blood_pressure),
                itemsLoading, BLOOD_PRESSURE));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS));
        groupItemEvaluations.add(new GroupItemEvaluation("Histórico de Saúde",
                itemsLoading, MEDICAL_RECORDS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY));
        groupItemEvaluations.add(new GroupItemEvaluation("Hábitos Físicos",
                itemsLoading, PHYSICAL_ACTIVITY));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Hábitos Alimentares",
                itemsLoading, FEEDING_HABITS));

        itemsLoading = new ArrayList<>();
        itemsLoading.add(new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS));
        groupItemEvaluations.add(new GroupItemEvaluation("Hábitos do Sono",
                itemsLoading, SLEEP_HABITS));

        initRecyclerView();
        downloadData();

    }

    /**
     * Prepare listNutritional data from the server.
     */
    private void prepareQuiz(NutritionalQuestionnaire nutritionalQuestionnaire) {

        GroupItemEvaluation groupItemEvaluation;
        ItemEvaluation itemEvaluation;

        //Sleep Habits
        groupItemEvaluation = getEvaluationGroupByType(SLEEP_HABITS);
        if (groupItemEvaluation != null) {
            itemEvaluation = groupItemEvaluation.getItems().get(0);
            if (nutritionalQuestionnaire.getSleepHabit() == null) {
                itemEvaluation.setTypeHeader(TYPE_EMPTY_REQUIRED);
            } else {
                nutritionalQuestionnaire.getSleepHabit().setCreatedAt(nutritionalQuestionnaire.getCreatedAt());
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setSleepHabit(lastNutritionalQuestionnaire.getSleepHabit());
            }
        }

        //Medical Records
        groupItemEvaluation = getEvaluationGroupByType(MEDICAL_RECORDS);
        if (groupItemEvaluation != null) {
            itemEvaluation = groupItemEvaluation.getItems().get(0);
            if (nutritionalQuestionnaire.getMedicalRecord() == null) {
                itemEvaluation.setTypeHeader(TYPE_EMPTY_REQUIRED);
            } else {
                nutritionalQuestionnaire.getMedicalRecord().setCreatedAt(nutritionalQuestionnaire.getCreatedAt());
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setMedicalRecord(lastNutritionalQuestionnaire.getMedicalRecord());
            }
        }

        //Feeding habits
        groupItemEvaluation = getEvaluationGroupByType(FEEDING_HABITS);
        if (groupItemEvaluation != null) {
            itemEvaluation = groupItemEvaluation.getItems().get(0);
            if (nutritionalQuestionnaire.getFeedingHabitsRecord() == null) {
                itemEvaluation.setTypeHeader(TYPE_EMPTY_REQUIRED);
            } else {
                nutritionalQuestionnaire.getFeedingHabitsRecord().setCreatedAt(nutritionalQuestionnaire.getCreatedAt());
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setFeedingHabitsRecord(lastNutritionalQuestionnaire.getFeedingHabitsRecord());
            }
        }

        //Physical Activity
        groupItemEvaluation = getEvaluationGroupByType(PHYSICAL_ACTIVITY);
        if (groupItemEvaluation != null) {
            itemEvaluation = groupItemEvaluation.getItems().get(0);
            if (nutritionalQuestionnaire.getPhysicalActivityHabit() == null) {
                itemEvaluation.setTypeHeader(TYPE_EMPTY_REQUIRED);
            } else {
                nutritionalQuestionnaire.getPhysicalActivityHabit().setCreatedAt(nutritionalQuestionnaire.getCreatedAt());
                itemEvaluation.setTypeHeader(TYPE_QUIZ);
                itemEvaluation.setPhysicalActivityHabit(lastNutritionalQuestionnaire.getPhysicalActivityHabit());
            }
        }
        evaluationAdapter.notifyDataSetChanged();
    }

    /**
     * Prepare item measurement from the server.
     *
     * @param type
     */
    private void prepareMeasurement(Measurement measurement, int type) {
        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);

        if (groupItemEvaluation == null) return;

        if (measurement == null && !groupItemEvaluation.getItems().isEmpty())
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_EMPTY_REQUIRED);
        else {
            validated.put(type, true);
            ItemEvaluation itemEvaluation = (ItemEvaluation) groupItemEvaluation.getItems().get(0).clone();
            groupItemEvaluation.getItems().clear();
            itemEvaluation.setTypeHeader(TYPE_MEASUREMENT);
            itemEvaluation.setMeasurement(measurement);
            groupItemEvaluation.getItems().add(itemEvaluation);
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
        } else if (type == ALL_QUIZ) {
            getEvaluationGroupByType(MEDICAL_RECORDS).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(PHYSICAL_ACTIVITY).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(SLEEP_HABITS).getItems().get(0).setTypeHeader(TYPE_ERROR);
            getEvaluationGroupByType(FEEDING_HABITS).getItems().get(0).setTypeHeader(TYPE_ERROR);
        } else {
            GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
            if (groupItemEvaluation == null) return;
            groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_ERROR);
        }
    }

    /**
     * Download data listNutritional from the server.
     */
    private void downloadData() {

        DisposableManager.add(haniotNetRepository
                .getLastNutritionalQuestionnaire(patient.get_id())
                .subscribe(nutritionalQuestionnaires -> {
                    Log.w("AAA", nutritionalQuestionnaires.toJson());
                    lastNutritionalQuestionnaire = nutritionalQuestionnaires;
                    prepareQuiz(nutritionalQuestionnaires);
                }, throwable -> {
                    Log.i("AAA", throwable.getMessage());
                    onDownloadError(ALL_QUIZ);
                }));

        DisposableManager.add(haniotNetRepository
                .getLastMeasurements(patient.get_id())
                .subscribe(measurents -> {
                    Log.w("AAA", measurents.toJson());
                    measurementLastResponse = measurents;
                    //  measurementList = measurents;
                    prepareMeasurements(measurents);
                }, throwable -> {
                    Log.i("AAA", throwable.getMessage());
                    onDownloadError(ALL_MEASUREMENT);
                }));

    }

    /**
     * Prepare measurements from Measurement Last Response from server.
     *
     * @param measurementLastResponse
     */
    private void prepareMeasurements(MeasurementLastResponse measurementLastResponse) {
        Log.w("AAA", "A " + measurementLastResponse);
        if (measurementLastResponse.getBloodPressure() != null && measurementLastResponse.getBloodPressure().get_id() != null)
            prepareMeasurement(measurementLastResponse.getBloodPressure(), BLOOD_PRESSURE);
        else prepareMeasurement(null, BLOOD_PRESSURE);
        if (measurementLastResponse.getWeight() != null && measurementLastResponse.getWeight().get_id() != null)
            prepareMeasurement(measurementLastResponse.getWeight(), WEIGHT);
        else prepareMeasurement(null, WEIGHT);
        if (measurementLastResponse.getBloodGlucose() != null && measurementLastResponse.getBloodGlucose().get_id() != null)
            prepareMeasurement(measurementLastResponse.getBloodGlucose(), GLUCOSE);
        else prepareMeasurement(null, GLUCOSE);
        if (measurementLastResponse.getWaistCircumference() != null && measurementLastResponse.getWaistCircumference().get_id() != null)
            prepareMeasurement(measurementLastResponse.getWaistCircumference(), WAIST_CIRCUMFERENCE);
        else prepareMeasurement(null, WAIST_CIRCUMFERENCE);
        if (measurementLastResponse.getHeight() != null && measurementLastResponse.getHeight().get_id() != null)
            prepareMeasurement(measurementLastResponse.getHeight(), HEIGHT);
        else prepareMeasurement(null, HEIGHT);
    }

    /**
     * Send nutritionalEvaluation for server.
     */
    private void sendEvaluation() {
        Log.i("AAA", "Preparing listNutritional...");
        nutritionalEvaluation.setPatient(patient);
        nutritionalEvaluation.setHealthProfessionalId(appPreferencesHelper.getUserLogged().get_id());
        nutritionalEvaluation.setPilotStudy(user.getPilotStudyIDSelected());

        leftFields = validated.get(GLUCOSE) != null
                && validated.get(BLOOD_PRESSURE) != null
                && validated.get(WAIST_CIRCUMFERENCE) != null
                && validated.get(WEIGHT) != null
                && validated.get(SLEEP_HABITS) != null
                && validated.get(PHYSICAL_ACTIVITY) == null
                && validated.get(MEDICAL_RECORDS) != null
                && validated.get(FEEDING_HABITS) != null;

        if (!leftFields) {
            nutritionalEvaluation.setFeedingHabits(lastNutritionalQuestionnaire.getFeedingHabitsRecord());
            nutritionalEvaluation.setPhysicalActivityHabits(lastNutritionalQuestionnaire.getPhysicalActivityHabit());
            nutritionalEvaluation.setMedicalRecord(lastNutritionalQuestionnaire.getMedicalRecord());
            nutritionalEvaluation.setSleepHabits(lastNutritionalQuestionnaire.getSleepHabit());

            List<Measurement> measurements = new ArrayList<>();
            measurements.add(measurementLastResponse.getBloodPressure());
            measurements.add(measurementLastResponse.getWaistCircumference());
            measurements.add(measurementLastResponse.getWeight());
            measurements.add(measurementLastResponse.getBloodGlucose());
            measurements.add(measurementLastResponse.getHeight());

            nutritionalEvaluation.setMeasurements(measurements);
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

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        Log.w("AAA", e.getMessage());
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

    @Override
    public void onAddItemClick(String name, int type) {
        Intent intent;
        switch (type) {
            case GLUCOSE:
                intent = new Intent(this, GlucoseActivity.class);
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
                intent = new Intent(this, HistoricQuizActivity.class);
                break;
            case PHYSICAL_ACTIVITY:
                intent = new Intent(this, HistoricQuizActivity.class);
                break;
            case FEEDING_HABITS:
                intent = new Intent(this, HistoricQuizActivity.class);
                break;
            case SLEEP_HABITS:
                intent = new Intent(this, HistoricQuizActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}
