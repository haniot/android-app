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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.ManagerMeasurementsActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.adapter.FragmentPageAdapter;
import br.edu.uepb.nutes.haniot.fragment.DashboardChartsFragment;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.SendMeasurementsEvent;
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
public class MainActivity extends AppCompatActivity implements DashboardChartsFragment.OnItemSelectedListener {
    private final String TAG = "MainActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;

    private String tabTitle;
    private String id = "";
    private String lastNameSelected = "";
    private Session session;

    private EventBus _eventBus;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.newMeasureButton)
    FloatingActionButton newMeasureButton;
    @BindView(R.id.floating_menu_main)
    FloatingActionMenu floatingMenu;
    @BindView(R.id.sendToServerButton)
    FloatingActionButton btnSendMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        this.session = new Session(getApplicationContext());

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        new FragmentPageAdapter(getSupportFragmentManager()).saveState();

        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        checkLastPatientAndUpdateTabTitle();

        this._eventBus = EventBus.getDefault();

        newMeasureButton.setOnClickListener(v -> {
            Intent it = new Intent(this, ManagerMeasurementsActivity.class);
            floatingMenu.close(false);
            startActivity(it);
        });
        btnSendMeasurement.setOnClickListener(c -> {

//            postEvent();
        });

        // BluetoohManager bluetoohManager = new BluetoohManager(this);
        // BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("1C:87:74:01:73:10");
        //bluetoohManager.connectDevice(device);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _eventBus.register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        _eventBus.unregister(this);
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
        if (floatingMenu.isOpened()) {
            floatingMenu.close(true);
        }
        checkLastPatientAndUpdateTabTitle();
    }

    /* Test if there is some patient saved on bundle or on shared preferences
        if some patient is finded, the title is updated.
    */
    public void checkLastPatientAndUpdateTabTitle() {
        //caso a tela seja restaurada
        Bundle params = getIntent().getExtras();
        if (params != null) {
            String idExtra = params.getString(getResources().getString(R.string.id_last_patient));
            String nameExtra = params.getString(getResources().getString(R.string.name_last_patient));
            if (idExtra != null && !idExtra.equals("")
                    && nameExtra != null && !nameExtra.equals("")) {
                this.id = idExtra;
                this.lastNameSelected = nameExtra;
            }

        } else {
            this.id = "";
            this.lastNameSelected = "";
        }
        String id = getResources().getString(R.string.id_last_patient);
        String name = getResources().getString(R.string.name_last_patient);
        String lastIdSelected = session.getString(id);
        String lastName = session.getString(name);
        if (!lastIdSelected.equals("") && !lastName.equals("")) {
            this.id = lastIdSelected;
            this.lastNameSelected = lastName;
        } else {
            this.id = "";
            this.lastNameSelected = "";
        }
        //caso nao tenha encontrado uma crianca selecionada no sharedpreferences

        tabTitle = getResources().getString(R.string.dashboard) + " - " + this.lastNameSelected;

        //Coloca o texto na aba

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                toast.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
        System.exit(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendMeasurements(final SendMeasurementsEvent e) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void postEvent() {
        EventBus.getDefault().post(new SendMeasurementsEvent());
    }

    @Override
    public void onItemSelected(String link) {
        updatfrag.updateName("Paola Leal");

    }

    public interface UpdateFrag {
        void updateName(String name);

        void updateMeasurement(Measurement measurement);

    }

    UpdateFrag updatfrag;

    public void updateApi(UpdateFrag listener) {
        updatfrag = listener;
    }
}
