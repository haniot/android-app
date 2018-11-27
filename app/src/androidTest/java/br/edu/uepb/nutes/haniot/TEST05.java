package br.edu.uepb.nutes.haniot;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import br.edu.uepb.nutes.haniot.activity.account.UpdateDataActivity;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummary;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitle;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.edu.uepb.nutes.haniot.activity.settings.MyPreferenceFragment.FORM_UPDATE;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsAnything.anything;


public class TEST05 {

    private UpdateDataActivity updateDataActivity;
    private Context context;

    @Rule
    public ActivityTestRule<UpdateDataActivity> myActivityTestRule = new ActivityTestRule
            <UpdateDataActivity>(UpdateDataActivity.class, true, false);

    private EditText name;
    private EditText email;

    @Before
    public void setUp()  {

        Intent it = new Intent();
        it.putExtra(FORM_UPDATE, true);
        myActivityTestRule.launchActivity(it);
        context = myActivityTestRule.getActivity().getApplicationContext();
        updateDataActivity = myActivityTestRule.getActivity();
        name = updateDataActivity.findViewById(R.id.edit_text_name);
        email = updateDataActivity.findViewById(R.id.edit_text_email);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void verifyComponents(){
        onView(withId(R.id.edit_text_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_email)).check(matches(isDisplayed()));
        onView(withId(R.id.action_save)).check(matches(isDisplayed()));
    }

    @Test
    public void task01empty() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_name)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNotNull(name.getError());
    }
    @Test
    public void task01size() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_name)).perform(clearText(), typeText("Te"),
                closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNotNull(name.getError());
    }
    @Test
    public void task01sucess() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_name)).perform(clearText(), typeText("Teste"),
                closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNull(name.getError());
    }

    @Test
    public void task02empty() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_email)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNotNull(email.getError());
    }
    @Test
    public void task02notValid() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_email)).perform(clearText(),
                typeText("testesnutesmail.com"), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNotNull(email.getError());
    }
    @Test
    public void task02valid() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_email)).perform(clearText(),
                typeText("testesnutes@mail.com"), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNull(email.getError());
    }
//    @Test
//    public void task03empty(){
//        onView(withId(R.id.edit_text_name)).perform(clearText(), closeSoftKeyboard());
//        onView(withId(R.id.edit_text_email)).perform(clearText(), closeSoftKeyboard());
//        onView(withId(R.id.action_save)).perform(click());
//        assertNotNull(name.getError());
//        assertNotNull(email.getError());
//    }
}
