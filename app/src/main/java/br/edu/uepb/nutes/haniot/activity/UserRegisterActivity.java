package br.edu.uepb.nutes.haniot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PatientsType;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

import static br.edu.uepb.nutes.haniot.data.model.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.UserType.HEALTH_PROFESSIONAL;
import static br.edu.uepb.nutes.haniot.data.model.UserType.PATIENT;

/**
 * UserRegisterActivity implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class UserRegisterActivity extends AppCompatActivity {

    final private String TAG = "UserRegisterActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.message_add)
    TextView messageAdd;

    @BindView(R.id.name_edittext)
    EditText nameEditText;

    @BindView(R.id.email_edittext)
    EditText emailEditText;

    @BindView(R.id.radio_group)
    RadioGroup genderGroup;

    @BindView(R.id.birth_edittext)
    EditText birthEditText;

    @BindView(R.id.address_edittext)
    EditText addressEditText;

    @BindView(R.id.phone_edittext)
    EditText phoneEditText;

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    @BindView(R.id.loading)
    ProgressBar progressBar;

    private Calendar myCalendar;
    private Patient patient;
    private AppPreferencesHelper appPreferencesHelper;
    private HaniotNetRepository haniotNetRepository;
    private CompositeDisposable compositeDisposable;
    private PatientDAO patientDAO;
    private boolean isEdit = false;
    private String oldEmail;
    private boolean editUserLogged;
    private HealthProfessional healthProfessional;
    private Admin admin;
    private User userLogged;
    private String name;
    private String address;
    private String phoneNumber;
    private String birthday;
    private String gender;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.patient_profile));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("action")) isEdit = true;
        if (getIntent().hasExtra("editUser")) editUserLogged = true;

        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);
    }

    /**
     * Validate fields.
     */
    private boolean validate() {
        boolean validated = true;
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError(getResources().getString(R.string.required_field));
            validated = false;
        }

        if (birthEditText.getText().toString().isEmpty()) {
            birthEditText.setError(getResources().getString(R.string.required_field));
            validated = false;
        }

        if (emailEditText.getText().length() > 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailEditText.setError(getResources().getString(R.string.validate_email));
            validated = false;
        }
        return validated;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void editAdmin() {
        Log.w("AAA", "editAdmin()");
        admin.set_id(id);
        admin.setName(nameEditText.getText().toString());
        if (isEdit) {
            if ((emailEditText.getText().toString().equals(userLogged.getEmail()))
                    || emailEditText.getText().toString().isEmpty()) {
                admin.setEmail(null);
            } else admin.setEmail(emailEditText.getText().toString());
        } else {
            if (emailEditText.getText().toString().isEmpty()) admin.setEmail(null);
            else admin.setEmail(emailEditText.getText().toString());
        }

        admin.setPhoneNumber(phoneEditText.getText().toString());
        admin.setBirthDate(DateUtils.formatDate(myCalendar.getTimeInMillis(), "yyyy-MM-dd"));
        Log.w("AAA", "editing Admin: " + admin.toJson());
        compositeDisposable.add(haniotNetRepository.updateAdmin(admin).subscribe(admin1 -> {
            showMessage(R.string.update_success);
            finish();
        }, this::errorHandler));

    }

    private void edithealthProfessional() {
        Log.w("AAA", "edithealthProfessional()");
        healthProfessional.set_id(id);
        healthProfessional.setName(nameEditText.getText().toString());

        if (isEdit) {
            if ((emailEditText.getText().toString().equals(userLogged.getEmail()))
                    || emailEditText.getText().toString().isEmpty()) {
                healthProfessional.setEmail(null);
            } else healthProfessional.setEmail(emailEditText.getText().toString());
        } else {
            if (emailEditText.getText().toString().isEmpty()) healthProfessional.setEmail(null);
            else healthProfessional.setEmail(emailEditText.getText().toString());
        }

        healthProfessional.setPhoneNumber(phoneEditText.getText().toString());
        healthProfessional.setBirthDate(DateUtils.formatDate(myCalendar.getTimeInMillis(), "yyyy-MM-dd"));
        Log.w("AAA", "editing Health Professional: " + healthProfessional.toJson());
        compositeDisposable.add(haniotNetRepository.updateHealthProfissional(healthProfessional).subscribe(healthProfessional1 -> {
            showMessage(R.string.update_success);
            finish();
        }, this::errorHandler));
    }

    /**
     * Save patient in App Preferences.
     */
    private void savePatient() {
        Log.w("AAA", "savePatient()");
        if (!isEdit) patient = new Patient();

        patient.setName(nameEditText.getText().toString());

        if (isEdit) {
            patient.set_id(id);
            if ((emailEditText.getText().toString().equals(patient.getEmail()))
                    || emailEditText.getText().toString().isEmpty()) {
                patient.setEmail(null);
            } else patient.setEmail(emailEditText.getText().toString());
        } else {
            if (emailEditText.getText().toString().isEmpty()) patient.setEmail(null);
            else patient.setEmail(emailEditText.getText().toString());
        }

        patient.setAddress(addressEditText.getText().toString());
        patient.setPhoneNumber(phoneEditText.getText().toString());
        patient.setBirthDate(DateUtils.formatDate(myCalendar.getTimeInMillis(), "yyyy-MM-dd"));
        Log.w("AAA", "patient BirthDate: " + patient.getBirthDate());
        if (genderGroup.getCheckedRadioButtonId() == R.id.male) {
            patient.setGender(PatientsType.GenderType.MALE);
        } else {
            patient.setGender(PatientsType.GenderType.FEMALE);
        }

        patient.setPilotId(userLogged.getPilotStudyIDSelected());
        Log.i(TAG, patient.toJson());

        if (isEdit) {
            Log.w("AAA", "patient to edit: " + patient.toJson());
            compositeDisposable.add(haniotNetRepository
                    .updatePatient(patient)
                    .doOnSubscribe(disposable -> showLoading(true))
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(patient1 -> {
                        patientDAO.save(patient);
                        showMessage(R.string.update_success);
                        finish();
                    }, this::errorHandler));
        } else {
            compositeDisposable.add(haniotNetRepository
                    .savePatient(patient)
                    .doAfterTerminate(() -> {
                        showLoading(false);
                        Log.i(TAG, "Salvando paciente no servidor!");
                    })
                    .doOnSubscribe(disposable -> {
                        Log.i(TAG, "Salvando paciente no servidor!");
                        showLoading(true);
                    })
                    .subscribe(patient -> {
                        if (patient.get_id() == null) {
                            showMessage(R.string.error_recover_data);
                            return;
                        }
                        this.patient.set_id(patient.get_id());
                        associatePatientToPilotStudy();
                    }, this::errorHandler));
        }
    }

    /**
     * Associate patient to selected pilot study in server.
     */
    private void associatePatientToPilotStudy() {
        compositeDisposable.add(haniotNetRepository
                .associatePatientToPilotStudy(userLogged.getPilotStudyIDSelected(), patient.get_id())
                .subscribe(o -> {
                    Log.w(TAG, "Patient associated to pilotstudy");
                    patientDAO.save(patient);
                    appPreferencesHelper.saveLastPatient(patient);
                    if (appPreferencesHelper.getUserLogged().getUserType().equals(HEALTH_PROFESSIONAL)) {
                        User user = appPreferencesHelper.getUserLogged();
                        if (user.getHealthArea().equals(getString(R.string.type_nutrition)))
                            startActivity(new Intent(UserRegisterActivity.this, QuizNutritionActivity.class));
                        else if (user.getHealthArea().equals(getString(R.string.type_dentistry)))
                            startActivity(new Intent(UserRegisterActivity.this, QuizOdontologyActivity.class));
                    }
                    finish();
                }, this::errorHandler));
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        Log.i(TAG, "errorHandler " + e.toString());
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            if (httpEx.code() == 409) {
                showMessage(R.string.error_409_patient);
            } else {
                showMessage(R.string.error_500);
            }
        } else showMessage(R.string.error_500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnectivity();
    }

    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Loading message,
     *
     * @param enabled boolean
     */
    private void showLoading(final boolean enabled) {
        runOnUiThread(() -> {
            fab.setEnabled(!enabled);
            if (enabled) progressBar.setVisibility(View.VISIBLE);
            else progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Validate patient.
     */
    View.OnClickListener fabClick = v -> {
        if (validate()) {
            Log.w("AAA", "Validated: " + validate());
            if (editUserLogged && userLogged.getUserType().equals(ADMIN)) editAdmin();
            else if (editUserLogged && userLogged.getUserType().equals(HEALTH_PROFESSIONAL))
                edithealthProfessional();
            else savePatient();
        }
    };

    /**
     * Prepare the view for editing the data
     */
    private void prepareEditing() {
        if (userLogged.getUserType().equals(PATIENT) || !editUserLogged) {
            compositeDisposable.add(haniotNetRepository
                    .getPatient(appPreferencesHelper.getLastPatient().get_id())
                    .doOnSubscribe(disposable -> {
                        enabledView(false);
                        showLoading(true);
                    })
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(patient1 -> {
                        id = patient1.get_id();
                        if (patient1.getEmail() != null) {
                            patient.setEmail(patient1.getEmail());
                            oldEmail = patient1.getEmail();
                        }
                        if (patient1.getName() != null) {
                            patient.setName(patient1.getName());
                            name = patient1.getName();
                        }
                        address = patient1.getAddress();
                        phoneNumber = patient1.getPhoneNumber();
                        birthday = patient1.getBirthDate();
                        gender = patient1.getGender();
                        prepareView();
                        enabledView(true);
                    }, this::errorHandler));
        } else if (userLogged.getUserType().equals(HEALTH_PROFESSIONAL)) {
            compositeDisposable.add(haniotNetRepository
                    .getHealthProfissional(userLogged.get_id())
                    .doOnSubscribe(disposable -> {
                        enabledView(false);
                        showLoading(true);
                    })
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(healthProfessional1 -> {
                        id = healthProfessional1.get_id();

                        if (healthProfessional1.getEmail() != null) {
                            healthProfessional.setEmail(healthProfessional1.getEmail());
                            oldEmail = healthProfessional1.getEmail();
                        }
                        if (healthProfessional1.getName() != null) {
                            healthProfessional.setName(healthProfessional1.getName());
                            name = healthProfessional1.getName();
                        }
                        phoneNumber = healthProfessional1.getPhoneNumber();
                        birthday = healthProfessional1.getBirthDate();
                        prepareView();
                        enabledView(true);
                    }, this::errorHandler));
        } else if (userLogged.getUserType().equals(ADMIN)) {
            compositeDisposable.add(haniotNetRepository
                    .getAdmin(userLogged.get_id())
                    .doOnSubscribe(disposable -> {
                        enabledView(false);
                        showLoading(true);
                    })
                    .doAfterTerminate(() -> showLoading(false))
                    .subscribe(admin1 -> {
                        id = admin1.get_id();

                        if (admin1.getEmail() != null) {
                            admin.setEmail(admin1.getEmail());
                            oldEmail = admin1.getEmail();
                        }
                        if (admin1.getName() != null) {
                            admin.setName(admin1.getName());
                            name = admin1.getName();
                        }
                        phoneNumber = admin1.getPhoneNumber();
                        birthday = admin1.getBirthDate();
                        prepareView();
                        enabledView(true);
                    }, this::errorHandler));
        }
    }

    /**
     * Check if you have connectivity.
     * If it does not, the elements in the view mounted to notify the user
     *
     * @return boolean
     */
    private boolean checkConnectivity() {
        if (!ConnectionUtils.internetIsEnabled(this)) {
            boxMessage.setVisibility(View.VISIBLE);
            messageError.setText(getString(R.string.error_connectivity));
            return false;
        }
        boxMessage.setVisibility(View.GONE);

        return true;
    }

    /**
     * Enable or disable view
     *
     * @param enabled boolean
     */
    private void enabledView(final boolean enabled) {
        runOnUiThread(() -> {
            nameEditText.setEnabled(enabled);
            emailEditText.setEnabled(enabled);
            addressEditText.setEnabled(enabled);
            phoneEditText.setEnabled(enabled);
            birthEditText.setEnabled(enabled);
            genderGroup.setEnabled(enabled);
        });
    }

    private void prepareView() {
        nameEditText.setText(name);
        emailEditText.setText(oldEmail);
        addressEditText.setText(address);
        phoneEditText.setText(phoneNumber);
        birthEditText.setText(DateUtils.formatDate(birthday, getString(R.string.date_format)));
        if (editUserLogged) {
            toolbar.setTitle(getResources().getString(R.string.user_profile));
            messageAdd.setText(getResources().getString(R.string.message_info_user));
            if (!(userLogged.getUserType().equals(PATIENT))) {
                addressEditText.setVisibility(View.GONE);
                genderGroup.setVisibility(View.GONE);
            }
        }

        Log.w("AAA", birthday);
        Log.w("AAA", DateUtils.convertStringDateToCalendar(birthday, null).getTime().toString());

        myCalendar = DateUtils.convertStringDateToCalendar(birthday, null);

        if (!editUserLogged)
            if (gender.equals(PatientsType.GenderType.MALE))
                genderGroup.check(R.id.male);
            else genderGroup.check(R.id.female);
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    private void showMessage(@StringRes int str) {
        String message = getString(str);
        if (message.isEmpty()) message = getString(R.string.error_500);

        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Init components.
     */
    private void initComponents() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        Log.i(TAG, appPreferencesHelper.getUserAccessHaniot().getAccessToken());
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        compositeDisposable = new CompositeDisposable();
        patientDAO = PatientDAO.getInstance(this);
        myCalendar = Calendar.getInstance();
        userLogged = appPreferencesHelper.getUserLogged();
        fab.setOnClickListener(fabClick);
        admin = new Admin();
        healthProfessional = new HealthProfessional();
        patient = new Patient();

        if (isEdit || editUserLogged) {
            prepareEditing();
        }


        birthEditText.setOnClickListener(v -> {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            DatePickerDialog dialog = new DatePickerDialog(UserRegisterActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        birthEditText.setText(DateUtils.formatDate(myCalendar.getTimeInMillis(),
                                getResources().getString(R.string.date_format)));
                    }, 2010, 1, 1);
            dialog.show();
        });
    }

    /**
     * On options item selected.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}