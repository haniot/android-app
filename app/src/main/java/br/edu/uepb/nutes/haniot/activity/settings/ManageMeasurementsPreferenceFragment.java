package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import br.edu.uepb.nutes.haniot.R;

public class ManageMeasurementsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_manage_measurements);

    }

}
