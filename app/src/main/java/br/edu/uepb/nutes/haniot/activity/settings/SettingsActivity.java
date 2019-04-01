package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;

/**
 * SettingsActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String SETTINGS_TYPE = "settings_type";
    public static final int SETTINGS_MAIN = 1;
    public static final int SETTINGS_MEASUREMENTS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get tne type of settings, 1 is to app configurations and 2 is to measurement configurations
        final int settingsType = getIntent().getIntExtra(SettingsActivity.SETTINGS_TYPE, 1);
        if (settingsType == SettingsActivity.SETTINGS_MAIN) {
            Objects.requireNonNull(actionBar)
                    .setTitle(getResources().getString(R.string.action_settings));
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainPreferenceFragment())
                    .commit();
        } else if (settingsType == SettingsActivity.SETTINGS_MEASUREMENTS) {
            Objects.requireNonNull(actionBar)
                    .setTitle(getResources().getString(R.string.settings_monitor_measurements));
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MeasurementsPreferenceFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}