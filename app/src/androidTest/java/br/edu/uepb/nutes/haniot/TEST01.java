package br.edu.uepb.nutes.haniot;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TEST01 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivity = new ActivityTestRule<LoginActivity>
            (LoginActivity.class,true, true);

    private EditText email;
    @Before
    public void setUp() throws Exception {
        LoginActivity loginActivity = mActivity.getActivity();
        email = loginActivity.findViewById(R.id.edit_text_email);
    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void task01() {
        onView(withId(R.id.edit_text_email)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(R.id.progressBarLogin)).check(matches(not((isDisplayed()))));
    }

    @Test
    public void task02NotValid(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                "arthurtebvamgmail.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        assertNotNull(email.getError());
    }
    @Test
    public void task02empty(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                "")).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        assertNotNull(email.getError());
    }
    @Test
    public void task02valid(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                "arthurtebvam@gmail.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        assertEquals(null, email.getError());
    }
    @Test
    public void task04(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                "arthurtebvam@gmail.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        onView(withId(R.id.progressBarLogin)).check(matches(withEffectiveVisibility(ViewMatchers.
                Visibility.VISIBLE)));
    }

//    @Test
//    public void task05(){
//        Intents.init();
//
//        Matcher<Intent> matcher = hasComponent(ChangeDataActivity.class.getName());
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.
//          RESULT_OK, null);
//        intending(matcher).respondWith(result);
//
//        onView(withId(R.id.text_view_signup)).perform(click());
//        intended(matcher);
//        Intents.release();
//    }
}
