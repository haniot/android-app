package br.edu.uepb.nutes.haniot.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.fragment.DashboardChartsFragment;
import br.edu.uepb.nutes.haniot.fragment.MeasurementsGridFragment;
import br.edu.uepb.nutes.haniot.utils.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.type.UserType.DENTISTRY;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.HEALTH_PROFESSIONAL;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.NUTRITION;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.PATIENT;

/**
 * Main activity, application start.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements DashboardChartsFragment.Communicator, View.OnClickListener {
    private final String LOG_TAG = "MainActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.frameCharts)
    FrameLayout frameChart;

    @BindView(R.id.frameMeasurements)
    FrameLayout frameMeasurements;

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.patient_actions)
    public FloatingActionMenu patientActionsMenu;

    @BindView(R.id.evaluation_nutrition)
    FloatingActionButton nutritioEvaluation;

    @BindView(R.id.quiz_odonto)
    FloatingActionButton quizOdonto;

    @BindView(R.id.quiz_nutrition)
    FloatingActionButton quizNutrition;

    private MeasurementsGridFragment measurementsGridFragment;
    private DashboardChartsFragment dashboardChartsFragment;
    private AppPreferencesHelper appPreferences;
    private Repository mRepository;
    private Patient patient;
    private long backPressed;
    private User userLogged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        toolbar.requestFocus();
        appPreferences = AppPreferencesHelper.getInstance(this);
        dashboardChartsFragment = DashboardChartsFragment.newInstance();
        measurementsGridFragment = MeasurementsGridFragment.newInstance();
        userLogged = appPreferences.getUserLogged();
        IntentFilter filterBluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filterBluetooth);
        IntentFilter filterInternet = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filterInternet);
        setupPatientActions();
        Log.w("AAA", "UserOB type: " + appPreferences.getUserAccessHaniot());
        Log.w("AAA", "UserOB: " + appPreferences.getUserLogged());

        mRepository = Repository.getInstance(this);
//        mRepository.syncronize();
    }

    private void loadDashboard() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameCharts, dashboardChartsFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameMeasurements, measurementsGridFragment)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verify the pilot is selected
        Log.w("AAA", "PilotStudyId: " + userLogged.getPilotStudyIDSelected());
        if ((userLogged.getPilotStudyIDSelected() == null || userLogged.getPilotStudyIDSelected().isEmpty())
                && !appPreferences.getUserLogged().getUserType().equals(PATIENT)) {
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            checkPatient();
        }
        mRepository.syncronize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * Init click listener of patient action buttons.
     */
    private void setupPatientActions() {

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                patientActionsMenu.hideMenu(true);
            } else {
                patientActionsMenu.showMenu(true);
            }
        });
        quizOdonto.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizOdontologyActivity.class));
            finish();
        });
        quizNutrition.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizNutritionActivity.class));
            finish();
        });
        nutritioEvaluation.setOnClickListener(v -> {
            Intent intent = new Intent(this, NutritionalEvaluationActivity.class);
            intent.putExtra("type", "nutrition");
            startActivity(intent);
        });

        if (appPreferences.getUserLogged().getUserType().equals(HEALTH_PROFESSIONAL)) {

            String healthArea = appPreferences.getUserLogged().getHealthArea();

            if (healthArea.equals(DENTISTRY)) {
                quizNutrition.setVisibility(View.GONE);
                nutritioEvaluation.setVisibility(View.GONE);

            } else if (healthArea.equals(NUTRITION)) {
                quizOdonto.setVisibility(View.GONE);

            } else if (healthArea.equals(PATIENT)) {
                patientActionsMenu.setVisibility(View.INVISIBLE);
            }
        } else if (appPreferences.getUserLogged().getUserType().equals(PATIENT)) {
            patientActionsMenu.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set patient selected.
     */
    public void checkPatient() {
        patient = appPreferences.getLastPatient();

        if (patient != null) {
            loadDashboard();
            checkPermissions();
        } else {
            if (!appPreferences.getUserLogged().getUserType().equals(PATIENT)) {
                startActivity(new Intent(this, ManagerPatientsActivity.class));
            }
        }
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    public void checkPermissions() {
        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Log.w(LOG_TAG, "checkPermissions(): Bluetooth desligado");
            dashboardChartsFragment.showMessageConnection("bluetooth", true);
            if (appPreferences.getBluetoothMode()) requestBluetoothEnable();
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        Log.w(LOG_TAG, "requestBluetoothEnable(): Criando intent");
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    public boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Location permission.
     */
    protected void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if ((requestCode == REQUEST_ENABLE_LOCATION) &&
                (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            showToast(getString(R.string.permission_location));
            requestLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                Log.w(LOG_TAG, "onActivityResult(): Bluetooth negado");
                appPreferences.saveBluetoothMode(false);
                dashboardChartsFragment.showMessageConnection("bluetooth", true);
            } else {
                Log.w(LOG_TAG, "onActivityResult(): Bluetooth aceito");
                appPreferences.saveBluetoothMode(true);
                requestLocationPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (appPreferences.getUserLogged().getUserType().equals(PATIENT))
            menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnManagePatient:
                startActivity(new Intent(getApplicationContext(), ManagerPatientsActivity.class));
                finish();
                break;
            case R.id.btnMenuMainSettings:
                Intent it = new Intent(this, SettingsActivity.class);
                it.putExtra(SettingsActivity.SETTINGS_TYPE, SettingsActivity.SETTINGS_MAIN);
                startActivity(it);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(final String menssage) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            this.finishAffinity();
        } else
            Toast.makeText(getBaseContext(), getString(R.string.back_confirm), Toast.LENGTH_SHORT).show();
        backPressed = System.currentTimeMillis();
    }

    /**
     * Notify new measurement received to dashboard.
     *
     * @param valueMeasurement {@link String}
     */
    @Override
    public void notifyNewMeasurement(String valueMeasurement) {
        dashboardChartsFragment.updateValueMeasurement(valueMeasurement);
    }

    @Override
    public void showMessage(int message) {
//        dashboardChartsFragment.showMessageConnection(message);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status = NetworkUtil.getConnectivityStatusString(context);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.w(LOG_TAG, "mReceiver: wifi desligado");
                    dashboardChartsFragment.showMessageConnection("wifi", true);
                } else {
                    Log.w(LOG_TAG, "mReceiver: wifi ligado");
                    dashboardChartsFragment.showMessageConnection("wifi", false);
                }
            }
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Log.w(LOG_TAG, "mReceiver: Bluetooth desligado");
                    dashboardChartsFragment.showMessageConnection("bluetooth", true);
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Log.w(LOG_TAG, "mReceiver: Bluetooth ligado");
                    appPreferences.saveBluetoothMode(true);
                    dashboardChartsFragment.showMessageConnection("bluetooth", false);
                }
            }
        }
    };

    public Patient getPatientSelected() {
        patient = appPreferences.getLastPatient();
        return patient;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.evaluation_nutrition:
                appPreferences.saveString("typeEvaluation", "nutrition");
                startActivity(new Intent(this, NutritionalEvaluationActivity.class));
                break;
            case R.id.quiz_nutrition:
                startActivity(new Intent(this, QuizNutritionActivity.class));
                break;
            case R.id.quiz_odonto:
                startActivity(new Intent(this, QuizOdontologyActivity.class));
                break;
            default:
        }
    }
}
