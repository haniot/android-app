package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.HistoricQuizAdapter;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.type.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_LOADING;
import static br.edu.uepb.nutes.haniot.data.model.ItemEvaluation.TYPE_QUIZ;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.FAMILY_COHESION;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.ORAL_HEALTH;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.SLEEP_HABITS;
import static br.edu.uepb.nutes.haniot.data.type.TypeEvaluation.SOCIODEMOGRAPHICS;
import static br.edu.uepb.nutes.haniot.data.type.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.type.UserType.DENTISTRY;
import static br.edu.uepb.nutes.haniot.data.type.UserType.NUTRITION;

public class HistoricQuizActivity extends AppCompatActivity implements HistoricQuizAdapter.OnClick {

    @BindView(R.id.list_quiz_nutrition)
    RecyclerView listNutritional;

    @BindView(R.id.list_quiz_odonto)
    RecyclerView listOdontological;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.box_not_nutrition)
    LinearLayout boxNotNutrition;

    @BindView(R.id.box_not_odontological)
    LinearLayout boxNotOdontological;

    @BindView(R.id.icon_error_nutrition)
    ImageView iconErrorNutrition;

    @BindView(R.id.text_error_nutrition)
    TextView textErrorNutrition;

    @BindView(R.id.icon_error_dentistry)
    ImageView iconErrorDentistry;

    @BindView(R.id.text_error_dentistry)
    TextView textErrorDentistry;

    private Repository mRepository;
    private CompositeDisposable mComposite;
    private AppPreferencesHelper appPreferencesHelper;
    private Patient patient;
    private List<GroupItemEvaluation> groupItemNutritionEvaluations;
    private List<GroupItemEvaluation> groupItemOdontologicalEvaluations;
    private HistoricQuizAdapter historicNutritionalAdapter;
    private HistoricQuizAdapter historicOdontologicalAdapter;
    private User user;

    @BindView(R.id.loading_nutrition)
    ProgressBar loadingNutrition;

    @BindView(R.id.loading_odontological)
    ProgressBar loadingOdontological;

    @BindView(R.id.category_1)
    TextView nutritionTitle;

    @BindView(R.id.title_category2)
    TextView dentistryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_quiz);
        ButterKnife.bind(this);
        initResources();
        initToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mComposite.dispose();
    }

    private void initResources() {
        groupItemNutritionEvaluations = new ArrayList<>();
        groupItemOdontologicalEvaluations = new ArrayList<>();
        mRepository = Repository.getInstance(this);
        mComposite = new CompositeDisposable();
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();
        user = appPreferencesHelper.getUserLogged();
        
        if (!user.getUserType().equals(ADMIN)) {
            if (user.getHealthArea().equals(DENTISTRY)) {
                nutritionTitle.setVisibility(View.GONE);
                loadingNutrition.setVisibility(View.GONE);
            } else if (user.getHealthArea().equals(NUTRITION)) {
                dentistryTitle.setVisibility(View.GONE);
                loadingOdontological.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Initialize toolbar.
     */
    private void initToolbar() {
        toolbar.setTitle("Histórico de Questionário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareItems();
    }

    /**
     * Download data listNutritional from the server.
     */
    private void downloadData() {
        if (user.getUserType().equals(ADMIN) || !user.getHealthArea().equals(DENTISTRY)) {
            mComposite.add(mRepository
                    .getAllNutritionalQuestionnaires(patient, 1, 100, "created_at")
                    .subscribe(nutritional -> {
                        Log.w("AAA", "Size Nutrition: " + nutritional.size());
                        showErrorDownloadData(false, NUTRITION);
                        setNutritionalGroups(nutritional);
                        loadingNutrition.setVisibility(View.GONE);
                    }, throwable -> {
                        loadingNutrition.setVisibility(View.GONE);
                        showErrorDownloadData(true, NUTRITION);
                    }));
        }

        if (user.getUserType().equals(ADMIN) || !user.getHealthArea().equals(NUTRITION)) {
            mComposite.add(mRepository
                    .getAllOdontologicalQuestionnaires(patient, 1, 100, "created_at")
                    .subscribe(odontological -> {
                        Log.w("AAA", "Size Odonto: " + odontological.size());
                        showErrorDownloadData(false, DENTISTRY);
                        setOdontologicalGroups(odontological);
                        loadingOdontological.setVisibility(View.GONE);
                    }, throwable -> {
                        loadingOdontological.setVisibility(View.GONE);
                        showErrorDownloadData(true, DENTISTRY);
                    }));
        }
    }

    /**
     * Prepare listNutritional data from the server.
     */
    private void setNutritionalItem(NutritionalQuestionnaire nutritionalQuestionnaire) {
        Log.w("BBBB", "setItem");
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos do Sono", TypeEvaluation.SLEEP_HABITS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setSleepHabit(nutritionalQuestionnaire.getSleepHabit());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Histórico de Saúde", TypeEvaluation.MEDICAL_RECORDS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setMedicalRecord(nutritionalQuestionnaire.getMedicalRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Alimentares", TypeEvaluation.FEEDING_HABITS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setFeedingHabitsRecord(nutritionalQuestionnaire.getFeedingHabitsRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Hábitos Físicos", TypeEvaluation.PHYSICAL_ACTIVITY);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setPhysicalActivityHabit(nutritionalQuestionnaire.getPhysicalActivityHabit());
        itemEvaluations.add(itemEvaluation);

        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation(DateUtils.convertDateTimeUTCToLocale(nutritionalQuestionnaire.getCreatedAt(), getString(R.string.datetime_format)),
                itemEvaluations, 1000, nutritionalQuestionnaire.get_id(), nutritionalQuestionnaire.getId());

        groupItemNutritionEvaluations.add(groupItemEvaluation);
    }


    /**
     * Prepare listNutritional data from the server.
     */
    private void setOdontologicalItem(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        Log.w("BBBB", "setItem");
        List<ItemEvaluation> itemEvaluations = new ArrayList<>();

        ItemEvaluation itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Sociodemográfico", SOCIODEMOGRAPHICS);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setSociodemographicRecord(odontologicalQuestionnaire.getSociodemographicRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Coesão Familiar", FAMILY_COHESION);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setFamilyCohesionRecord(odontologicalQuestionnaire.getFamilyCohesionRecord());
        itemEvaluations.add(itemEvaluation);

        itemEvaluation = new ItemEvaluation(R.drawable.action_quiz, TYPE_LOADING,
                "Saúde Bucal", ORAL_HEALTH);
        itemEvaluation.setTypeHeader(TYPE_QUIZ);
        itemEvaluation.setOralHealthRecord(odontologicalQuestionnaire.getOralHealthRecord());
        itemEvaluations.add(itemEvaluation);


        GroupItemEvaluation groupItemEvaluation = new GroupItemEvaluation("Respondido em " + DateUtils.convertDateTimeUTCToLocale(odontologicalQuestionnaire.getCreatedAt(), getString(R.string.datetime_format)),
                itemEvaluations, 1000, odontologicalQuestionnaire.get_id(), odontologicalQuestionnaire.getId());

        groupItemOdontologicalEvaluations.add(groupItemEvaluation);
    }

    private void setNutritionalGroups(List<NutritionalQuestionnaire> nutritionalQuestionnaires) {

        groupItemNutritionEvaluations.clear();
        for (NutritionalQuestionnaire nutritionalQuestionnaire : nutritionalQuestionnaires) {
            Log.w("AAA", nutritionalQuestionnaire.toString());
            setNutritionalItem(nutritionalQuestionnaire);
        }
        if (nutritionalQuestionnaires.isEmpty()) {
            showEmpty(true, NUTRITION);
        }
        listNutritional.setAdapter(null);
        initRecyclerView();
    }

    private void setOdontologicalGroups(List<OdontologicalQuestionnaire> odontologicalQuestionnaires) {

        groupItemOdontologicalEvaluations.clear();
        for (OdontologicalQuestionnaire odontologicalQuestionnaire : odontologicalQuestionnaires) {
            Log.w("AAA", odontologicalQuestionnaire.toString());
            setOdontologicalItem(odontologicalQuestionnaire);
        }
        if (odontologicalQuestionnaires.isEmpty()) {
            showEmpty(true, DENTISTRY);
        }
        listOdontological.setAdapter(null);
        initRecyclerView();
    }

//    /**
//     * Show error.
//     *
//     * @param
//     */
//    private void onDownloadError(int type) {
//        GroupItemEvaluation groupItemEvaluation = getEvaluationGroupByType(type);
//        if (groupItemEvaluation == null) return;
//        groupItemEvaluation.getItems().get(0).setTypeHeader(TYPE_ERROR);
//    }

//    /**
//     * Get get listNutritional group object by type.
//     *
//     * @param type
//     * @return
//     */
//    private GroupItemEvaluation getEvaluationGroupByType(int type) {
//        for (GroupItemEvaluation groupItemEvaluation : groupItemNutritionEvaluations) {
//            if (groupItemEvaluation.getType() == type) {
//                return groupItemEvaluation;
//            }
//        }
//        return null;
//    }

    /**
     * Show recyclerview items in prepareItems mode.
     */
    private void prepareItems() {
        groupItemOdontologicalEvaluations.clear();
        groupItemNutritionEvaluations.clear();
        downloadData();
    }

    /**
     * Show message empty getAllByUserId.
     */
    private void showEmpty(boolean enabled, String type) {
        if (type.equals(NUTRITION)) {
            iconErrorNutrition.setImageResource(R.drawable.ic_not_form);
            textErrorNutrition.setText(getString(R.string.nutrition_quiz_empty));
            if (enabled) boxNotNutrition.setVisibility(View.VISIBLE);
            else boxNotNutrition.setVisibility(View.GONE);
        } else if (type.equals(DENTISTRY)) {
            iconErrorDentistry.setImageResource(R.drawable.ic_not_form);
            textErrorDentistry.setText(getString(R.string.dentistry_quiz_empty));
            if (enabled) boxNotOdontological.setVisibility(View.VISIBLE);
            else boxNotOdontological.setVisibility(View.GONE);
        }
    }

    /**
     * Show message error download data.
     */
    private void showErrorDownloadData(boolean enabled, String type) {
        if (type.equals(NUTRITION)) {
            iconErrorNutrition.setImageResource(R.drawable.ic_error_server);
            textErrorNutrition.setText(getText(R.string.error_recover_data));
            if (enabled) boxNotNutrition.setVisibility(View.VISIBLE);
            else boxNotNutrition.setVisibility(View.GONE);
        } else if (type.equals(DENTISTRY)) {
            iconErrorDentistry.setImageResource(R.drawable.ic_error_server);
            textErrorDentistry.setText(getText(R.string.error_recover_data));
            if (enabled) boxNotOdontological.setVisibility(View.VISIBLE);
            else boxNotOdontological.setVisibility(View.GONE);
        }
    }

    /**
     * Initialize NutritionalEvaluationOB recyclerview.
     */
    private void initRecyclerView() {
        historicNutritionalAdapter = new HistoricQuizAdapter(groupItemNutritionEvaluations, this);
        historicOdontologicalAdapter = new HistoricQuizAdapter(groupItemOdontologicalEvaluations, this);
        listNutritional.setLayoutManager(new LinearLayoutManager(this));
        listOdontological.setLayoutManager(new LinearLayoutManager(this));
        listNutritional.setNestedScrollingEnabled(false);
        listOdontological.setNestedScrollingEnabled(false);
        historicNutritionalAdapter.setListener(this);
        historicOdontologicalAdapter.setListener(this);
        listNutritional.setAdapter(historicNutritionalAdapter);
        listOdontological.setAdapter(historicOdontologicalAdapter);
    }

    @Override
    public void onAddItemClick(String name, int type) {

    }

    @Override
    public void onAddItemClick(String name, int type, String _idQuiz, long idQuiz) {
        Intent intent = null;

        switch (type) {
            case MEDICAL_RECORDS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", MEDICAL_RECORDS);
                break;
            case SLEEP_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", SLEEP_HABITS);
                break;
            case FEEDING_HABITS:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", FEEDING_HABITS);
                break;
            case PHYSICAL_ACTIVITY:
                intent = new Intent(this, QuizNutritionActivity.class);
                intent.putExtra("checkpoint", PHYSICAL_ACTIVITY);
                break;
            case FAMILY_COHESION:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", FAMILY_COHESION);
                break;
            case ORAL_HEALTH:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", ORAL_HEALTH);
                break;
            case SOCIODEMOGRAPHICS:
                intent = new Intent(this, QuizOdontologyActivity.class);
                intent.putExtra("checkpoint", SOCIODEMOGRAPHICS);
                break;
        }
        if (intent != null) {
            if (_idQuiz != null && !ConnectionUtils.internetIsEnabled(this)) {
                Toast.makeText(this, getString(R.string.connect_network_try_again), Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("_idUpdate", _idQuiz);
                intent.putExtra("idUpdate", idQuiz);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSelectClick(ItemEvaluation itemEvaluation, String idQuiz) {

    }

}
