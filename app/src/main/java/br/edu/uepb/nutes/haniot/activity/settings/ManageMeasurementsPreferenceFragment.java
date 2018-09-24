package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

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

    public void initPreferences() {

        boolean bloodPressureMonitor = session.getBoolean("blood_pressure_monitor");
        boolean heartRateH7 = session.getBoolean("heart_rate_sensor_polar_h7");
        boolean heartRateH10 = session.getBoolean("heart_rate_sensor_polar_h10");
        boolean smartBand = session.getBoolean("smart_band");
        boolean earThermometer = session.getBoolean("ear_thermometer");
        boolean accuCheck = session.getBoolean("accu_check");
        boolean bodyCompositionYunmai = session.getBoolean("body_composition_yunmai");
        boolean bodyCompositionOmron = session.getBoolean("body_composition_omron");


//        The expression (boolean) newValue return true if the switch is checked
        SwitchPreference bloodPressure = (SwitchPreference)
                findPreference(getString(R.string.key_blood_pressure));
        bloodPressure.setChecked(bloodPressureMonitor);
        bloodPressure.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((boolean) newValue) session.putBoolean("blood_pressure_monitor", true);
            else session.putBoolean("blood_pressure_monitor", false);
            return true;
        });

        SwitchPreference heartH7 = (SwitchPreference) findPreference("key_heart_H7");
        heartH7.setChecked(heartRateH7);
        heartH7.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((boolean) newValue) {
                session.putBoolean("heart_rate_sensor_polar_h7", true);
            } else {
                session.putBoolean("heart_rate_sensor_polar_h7", false);
            }
            return true;
        });

        SwitchPreference heartH10 = (SwitchPreference) findPreference("key_heart_H10");
        heartH10.setChecked(heartRateH10);
        heartH10.setOnPreferenceChangeListener((preference, newValue) -> {

            if ((boolean) newValue) session.putBoolean("heart_rate_sensor_polar_h10", true);
            else session.putBoolean("heart_rate_sensor_polar_h10", false);
            return true;
        });

        SwitchPreference smart_band = (SwitchPreference) findPreference("key_smartband");
        smart_band.setChecked(smartBand);
        smart_band.setOnPreferenceChangeListener((preference, newValue) -> {

            if ((boolean)newValue) session.putBoolean("smart_band",true);
            else session.putBoolean("smart_band",false);
            return true;
        });

        SwitchPreference thermometer = (SwitchPreference)
                findPreference("key_ear_thermometer");
        thermometer.setChecked(earThermometer);
        thermometer.setOnPreferenceChangeListener((preference, newValue) -> {

            if ((boolean)newValue) session.putBoolean("ear_thermometer",true);
            else session.putBoolean("ear_thermometer",false);
            return true;
        });

        SwitchPreference accu = (SwitchPreference) findPreference("key_accu_check");
        accu.setChecked(accuCheck);
        accu.setOnPreferenceChangeListener((preference,newValue) -> {

            if ((boolean)newValue) session.putBoolean("accu_check",true);
            else session.putBoolean("accu_check",false);
            return true;
        });

        SwitchPreference yunmai = (SwitchPreference) findPreference("key_yunmai");
        yunmai.setChecked(bodyCompositionYunmai);
        yunmai.setOnPreferenceChangeListener((preference,newValue) -> {

            if ((boolean)newValue) session.putBoolean("body_composition_yunmai",true);
            else session.putBoolean("body_composition_yunmai",false);
            return true;
        });

        SwitchPreference omron = (SwitchPreference) findPreference("key_omron");
        omron.setChecked(bodyCompositionOmron);
        omron.setOnPreferenceChangeListener((preference,newValue) -> {

            if((boolean)newValue) session.putBoolean("body_composition_omron",true);
            else session.putBoolean("body_composition_omron",false);
            return true;
        });

    }

}
