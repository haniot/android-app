package br.edu.uepb.nutes.haniot;


import android.content.Context;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TEST01 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivity = new ActivityTestRule<LoginActivity>
            (LoginActivity.class,true, true);

    private EditText email;
    private EditText password;
    private Context context;
    private String emailLogin = "testenutes@mail.com";
    private String senhaLogin = "abc*123";

    @Before
    public void setUp() throws Exception {
        LoginActivity loginActivity = mActivity.getActivity();
        context = mActivity.getActivity();
        email = loginActivity.findViewById(R.id.edit_text_email);
        password = loginActivity.findViewById(R.id.edit_text_password);
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
        onView(withId(R.id.progressBarLogin)).check(matches(withEffectiveVisibility(ViewMatchers.
                Visibility.GONE)));
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
        onView(withId(R.id.edit_text_password)).perform(typeText(senhaLogin)).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        assertNotNull(email.getError());
    }

    @Test
    public void task02valid(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                emailLogin)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        assertEquals(null, email.getError());
    }

    @Test
    public void task03(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                emailLogin)).perform(closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        assertNotNull(password.getError());
    }

    @Test
    public void task04(){
        onView(withId(R.id.edit_text_email)).perform(typeText(
                emailLogin)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText("1254Art@")).
                perform(closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
        onView(withId(R.id.progressBarLogin)).check(matches(withEffectiveVisibility(ViewMatchers.
                Visibility.VISIBLE)));
    }

    @Test
    public void task05() throws InterruptedException {
        onView(withId(R.id.edit_text_email)).perform(typeText(
                emailLogin)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText(
                senhaLogin)).perform(closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        Thread.sleep(10000);
                Session session = new Session(context);
                assertTrue(session.isLogged());
                assertTrue(session.getUserLogged().getEmail().equals(emailLogin));
    }

}
