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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.fragment.DashboardChartsFragment;
import br.edu.uepb.nutes.haniot.fragment.MeasurementsGridFragment;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, application start.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MainActivity extends AppCompatActivity implements DashboardChartsFragment.Communicator {
    private final String TAG = "MainActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;

    private Session session;
    Patient patient;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.viewpager)
//    ViewPager viewPager;

    @BindView(R.id.frameCharts)
    FrameLayout frameChart;

    @BindView(R.id.frameMeasurements)
    FrameLayout frameMeasurements;

    MeasurementsGridFragment measurementsGridFragment;
    DashboardChartsFragment dashboardChartsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.session = new Session(getApplicationContext());

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        hasPermissions();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction chartTransition = fragmentManager.beginTransaction();
        dashboardChartsFragment = new DashboardChartsFragment();
        chartTransition.replace(R.id.frameCharts, dashboardChartsFragment);
        chartTransition.commit();
        FragmentTransaction measurementsTransition = fragmentManager.beginTransaction();
        measurementsGridFragment = new MeasurementsGridFragment();
        measurementsTransition.replace(R.id.frameMeasurements, measurementsGridFragment);
        measurementsTransition.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPatient();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * User not logged
         */
        if (!(new Session(this).isLogged())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        checkPatient();
    }

    /* Test if there is some patient saved on bundle or on shared preferences
        if some patient is finded, the title is updated.
    */
    public void checkPatient() {
        patient = PatientDAO.getInstance(this)
                .getFromID(session.getString(getString(R.string.id_last_patient)));

        if (patient != null) {
            dashboardChartsFragment.updateNamePatient(patient);
        } else {
            showToast("Nenhum paciente selecionado!");
            startActivity(new Intent(this, ManagePatientsActivity.class));
        }
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     *
     * @return boolean
     */
    private boolean hasPermissions() {
        if (!ConnectionUtils.bluetoothIsEnabled()) {
            requestBluetoothEnable();
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        if (!ConnectionUtils.bluetoothIsEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    private boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return true;
    }

    /**
     * Request Location permission.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_location, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode != Activity.RESULT_OK)
            finish();
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

    @Override
    public void respond(String data) {
        dashboardChartsFragment.updateValueMeasurement(data);
    }

}
