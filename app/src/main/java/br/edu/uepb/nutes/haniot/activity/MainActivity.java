package br.edu.uepb.nutes.haniot.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.adapter.FragmentPageAdapter;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
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
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION  = 2;

    private String tabTitle          = "DASHBOARD";
    private String id                =          "";
    private SharedPreferences                prefs;
    private SharedPreferences.Editor        editor;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.newMeasureButton)
    FloatingActionButton newMeasureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefs = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = prefs.edit();

        toolbar.setTitle("HANIoT");
        setSupportActionBar(toolbar);

        new FragmentPageAdapter(getSupportFragmentManager()).saveState();

        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        checkLastChildAndUpdateTabTitle();

        newMeasureButton.setOnClickListener(v -> {
            Intent it = new Intent(MainActivity.this, ManageMeasurements.class);
            startActivity(it);
        });
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
        checkLastChildAndUpdateTabTitle();
    }

    /*Testa se tem criança salva no bundle ou no sharedpreferences,
        caso tenha salva o id da criança e atualiza o titulo da aba
    */
    public void checkLastChildAndUpdateTabTitle(){
        Bundle params = getIntent().getExtras();
        if (params != null){
            String idExtra = params.getString("id_child");
            if (idExtra  != null && !idExtra .equals("")) {
                this.id = idExtra;
            }
        }else{
            this.id = "";
        }
        String lastId = prefs.getString("childId","null");
        if (!lastId.equals("null")){
            this.id = lastId;
        }else{
            this.id = "";
        }
        if (this.id.equals("")){
            this.id = "404 not found";
            tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.colorRed));
        }

        tabTitle = "DASHBOARD" + " - " + id;

        if (tabLayout.getTabAt(0) != null){

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
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.btnManageChildren:
                startActivity(new Intent(getApplicationContext(),ManageChildrenActivity.class));
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

}
