package br.edu.uepb.nutes.haniot;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;


public class TEST02 {

    private SettingsActivity settingsActivity;
    private Context context;

    @Rule
    public ActivityTestRule<SettingsActivity> myActivityTestRule = new ActivityTestRule
            <SettingsActivity>(SettingsActivity.class, true, false);

    @Before
    public void setUp()  {

        Intent it = new Intent();
        it.putExtra("settingType", 1);
        myActivityTestRule.launchActivity(it);
        context = myActivityTestRule.getActivity().getApplicationContext();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void task01(){

    }
}
