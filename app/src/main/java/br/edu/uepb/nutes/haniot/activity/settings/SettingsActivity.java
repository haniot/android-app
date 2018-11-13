package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.utils.Log;

/**
 * SettingsActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        Get tne type of settings, 1 is to app configurations and 2 is to measurement
//        configurations
        final Intent intent = getIntent();
        if (intent != null){
            int result = intent.getIntExtra("settingType",0);
            if (result == 1){
                actionBar.setTitle(getResources().getString(R.string.action_settings));
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new MyPreferenceFragment()).commit();
            }else if(result == 2){
                actionBar.setTitle(getResources().getString(R.string.manage_measurement_title));
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new ManageMeasurementsPreferenceFragment())
                        .commit();
            }else{
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}