package br.edu.uepb.nutes.haniot.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.adapter.FragmentPageAdapter;
import br.edu.uepb.nutes.haniot.model.SendMeasurementsEvent;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.ble.data.Data;

/**
 * Main activity, application start.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MainActivity extends AppCompatActivity {
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
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
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
        tabLayout.setupWithViewPager(viewPager);

        checkLastPatientAndUpdateTabTitle();

        this._eventBus = EventBus.getDefault();

        newMeasureButton.setOnClickListener(v -> {
            Intent it = new Intent(this, SettingsActivity.class);
            it.putExtra("settingType", 2);
            floatingMenu.close(false);
            startActivity(it);
        });
        btnSendMeasurement.setOnClickListener(c -> {
            postEvent();
        });
        Manager manager = new Manager(this);
        manager.setGattCallbacks(bleManagerCallbacks);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("1C:87:74:01:73:10");
        manager.connectDevice(device);
    }

    ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@androidx.annotation.NonNull BluetoothDevice device, @androidx.annotation.NonNull Data data) {

//            if (data.size() < 5) {
//                onInvalidDataReceived(device, data);
//                return;
//            }

            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset);
         //   final int unit = (flags & 0x01) == UNIT_C ? UNIT_C : UNIT_F;
            final boolean timestampPresent = (flags & 0x02) != 0;
            final boolean temperatureTypePresent = (flags & 0x04) != 0;
            offset += 1;


            final float temperature = data.getFloatValue(Data.FORMAT_FLOAT, 1);
            offset += 4;

            Calendar calendar = null;
//            if (timestampPresent) {
//                calendar = DateTimeDataCallback.readDateTime(data, offset);
//                offset += 7;
//            }

            Integer type = null;
            if (temperatureTypePresent) {
                type = data.getIntValue(Data.FORMAT_UINT8, offset);
                // offset += 1;
            }

            Log.i(TAG, "Received measurent from " + device.getName() + ": " + temperature);


        }

        @Override
        public void onDeviceConnecting(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
            showToast("Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            showToast("Connected to " + device.getName());

        }

        @Override
        public void onDeviceDisconnecting(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
            showToast("Disconnecting from " + device.getName());

        }

        @Override
        public void onDeviceDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            showToast("Disconnected to " + device.getName());

        }

        @Override
        public void onLinkLossOccurred(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(@androidx.annotation.NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Log.i(TAG, "Services Discovered from " + device.getName());
            showToast("Services Discovered from " + device.getName());

        }

        @Override
        public void onDeviceReady(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingRequired(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBonded(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingFailed(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onError(@androidx.annotation.NonNull BluetoothDevice device, @androidx.annotation.NonNull String message, int errorCode) {
            Log.i(TAG, "Error from " + device.getName() + " - " + message);
            showToast("Error from " + device.getName() + " - " + message);

        }

        @Override
        public void onDeviceNotSupported(@androidx.annotation.NonNull BluetoothDevice device) {

        }
    };

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
        if (this.id.equals("")) {
            this.id = getResources().getString(R.string.noPatientSelected);
            this.lastNameSelected = getResources().getString(R.string.noPatientSelected);
            tabLayout.setTabTextColors(ContextCompat
                    .getColorStateList(this, R.color.colorRed));
        }

        tabTitle = getResources().getString(R.string.dashboard) + " - " + this.lastNameSelected;

        //Coloca o texto na aba
        if (tabLayout.getTabAt(0) != null) {

            SpannableString dash = new SpannableString(tabTitle);
            dash.setSpan(new StyleSpan(Typeface.BOLD), tabTitle.length(), dash.length(), 0);
            tabLayout.getTabAt(0).setText(dash);
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
}
