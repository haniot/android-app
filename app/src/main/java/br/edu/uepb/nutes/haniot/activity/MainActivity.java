package br.edu.uepb.nutes.haniot.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.fragment.DashboardChartsFragment;
import br.edu.uepb.nutes.haniot.fragment.MeasurementsGridFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, application start.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements DashboardChartsFragment.Communicator {
    private final String TAG = "MainActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frameCharts)
    FrameLayout frameChart;
    @BindView(R.id.frameMeasurements)
    FrameLayout frameMeasurements;

    private MeasurementsGridFragment measurementsGridFragment;
    private DashboardChartsFragment dashboardChartsFragment;
    private AppPreferencesHelper appPreferences;
    private Patient patient;

    /**
     * On create.
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        appPreferences = AppPreferencesHelper.getInstance(this);

        dashboardChartsFragment = DashboardChartsFragment.newInstance();
        measurementsGridFragment = MeasurementsGridFragment.newInstance();
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

    /**
     * On start.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // User not logged
        if (!(new Session(this).isLogged())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();

            return;
        }

        checkPermissions();

        // Verify the pilot is selected
        if (appPreferences.getLastPilotStudy() == null) {
            //TODO mudar antes do push
            startActivity(new Intent(this, ManagePatientsActivity.class));
            //startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            // Verify the patient is selected

            loadDashboard();
        }
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    private void checkPermissions() {
        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            requestBluetoothEnable();
        }

        if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    private boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Location permission.
     */
    private void requestLocationPermission() {
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode != Activity.RESULT_OK) {
            requestBluetoothEnable();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnManagePatient:
                startActivity(new Intent(getApplicationContext(), ManagePatientsActivity.class));
                break;
            case R.id.btnMenuMainSettings:
                Intent it = new Intent(this, SettingsActivity.class);
                it.putExtra("settingType", 1);
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
        super.onBackPressed();
        this.finishAffinity();
        System.exit(0);
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

    public Patient getPatientSelected() {
        return patient;
    }
}
