package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import br.edu.uepb.nutes.haniot.R;

public class ManageMeasurementsPreferenceFragment extends PreferenceFragment {

    private Session session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getActivity());
        addPreferencesFromResource(R.xml.pref_manage_measurements);

        initPreferences();

    }

    public void initPreferences(){

        boolean bloodPressureMonitor = session.getBoolean("blood_pressure_monitor");
        boolean heartRateH10 = session.getBoolean("heart_rate_sensor_polar_h10");
        boolean heartRateH7 = session.getBoolean("heart_rate_sensor_polar_h7");
        boolean smartBand = session.getBoolean("smart_band");
        boolean earThermometer = session.getBoolean("ear_thermometer");
        boolean accuCheck = session.getBoolean("accu_check");
        boolean bodyCompositionYunmai = session.getBoolean("body_composition_yunmai");
        boolean bodyCompositionOmron = session.getBoolean("body_composition_omron");

        SwitchPreference bloodPressure = (SwitchPreference)
                findPreference(getString(R.string.key_blood_pressure));
        bloodPressure.setChecked(bloodPressureMonitor);
        bloodPressure.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean) newValue) session.putBoolean("blood_pressure_monitor", true);
                    else session.putBoolean("blood_pressure_monitor", false);
                return true;
            }
        });

    }

}
