package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

import br.edu.uepb.nutes.haniot.R;

public class ManageMeasurementsPreferenceFragment extends PreferenceFragment {

    private Session session;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getActivity());
        addPreferencesFromResource(R.xml.pref_manage_measurements);
        PreferenceManager.setDefaultValues(
                getActivity(),R.xml.pref_manage_measurements, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    public Boolean getPreferenceBoolean(String key){
        return preferences.getBoolean(key, false);
    }

}
