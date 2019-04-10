package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import br.edu.uepb.nutes.haniot.R;

/**
 * Measurements Preference implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MeasurementsPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.measurements_preferences);
    }
}
