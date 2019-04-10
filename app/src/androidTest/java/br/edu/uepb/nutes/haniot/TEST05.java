package br.edu.uepb.nutes.haniot;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import br.edu.uepb.nutes.haniot.activity.account.UpdateDataActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static br.edu.uepb.nutes.haniot.activity.settings.MainPreferenceFragment.FORM_UPDATE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


public class TEST05 {

    private UpdateDataActivity updateDataActivity;
    private Context context;

    @Rule
    public ActivityTestRule<UpdateDataActivity> myActivityTestRule = new ActivityTestRule
            <UpdateDataActivity>(UpdateDataActivity.class, true, false);

    private EditText name;
    private EditText email;
    private Session session;
    private String afterEmail, afterName;
    private Intent it ;

    @Before
    public void setUp()  {
        it = new Intent();
        it.putExtra(FORM_UPDATE, true);
        myActivityTestRule.launchActivity(it);
        context = myActivityTestRule.getActivity().getApplicationContext();
        updateDataActivity = myActivityTestRule.getActivity();
        name = updateDataActivity.findViewById(R.id.edit_text_name);
        email = updateDataActivity.findViewById(R.id.edit_text_email);
        session = new Session(context);
        afterEmail = "testes@nutes.com";
        afterName = "Testes Nutes";
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test components on the screen
     * **/

    @Test
    public void verifyComponents(){
        onView(withId(R.id.edit_text_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_email)).check(matches(isDisplayed()));
        onView(withId(R.id.action_save)).check(matches(isDisplayed()));
    }

    /**
     * Not accept empty name field
     * **/

    @Test
    public void task01empty() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_name)).perform(clearText(), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
        assertNotNull(name.getError());
    }

    /**
     * Not accept name size < 3 characters
     * **/

    @Test
    public void task01size() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_name)).perform(clearText(), typeText("Te"),
                    closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
        assertNotNull(name.getError());
    }

    /**
     * Test if name valid, is changed
     * **/

    @Test
    public void task01sucess() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_name)).perform(clearText(), typeText("Teste"),
                    closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
        assertNull(name.getError());
        assertEquals("Teste", session.getUserLogged().getName());
    }

    /**
     * Not accept empty email field
     * **/

    @Test
    public void task02empty() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_email)).perform(clearText(), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
        assertNotNull(email.getError());
    }

    /**
     * Not accept invalid email
     * **/

    @Test
    public void task02notValid() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.edit_text_email)).perform(clearText(),
                typeText("testesnutesmail.com"), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        assertNotNull(email.getError());
    }

    /**
     * Test if email valid, is changed
     * **/

    @Test
    public void task02valid() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_email)).perform(clearText(),
                    typeText("testemail@mail.com"), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
            assertNull(email.getError());

        Thread.sleep(2000);
            assertEquals("testemail@mail.com", session.getUserLogged().getEmail());

        Thread.sleep(5000);
            myActivityTestRule.launchActivity(it);
            onView(withId(R.id.edit_text_email)).perform(clearText(),
                    typeText(afterEmail), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
    }

    /**
     * If empty fields do not change
     * **/

    @Test
    public void task03empty() throws InterruptedException {
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_name)).perform(clearText(), closeSoftKeyboard());
            onView(withId(R.id.edit_text_email)).perform(clearText(), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
        assertNotNull(name.getError());
    }

    /**
     * Change email and name
     * **/

    @Test
    public void task03valid() throws InterruptedException{
        Thread.sleep(1000);
            onView(withId(R.id.edit_text_name)).perform(clearText(), typeText(
                    "Name"), closeSoftKeyboard());
            onView(withId(R.id.edit_text_email)).perform(clearText(), typeText(
                    "email@testes.com"),
                    closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());

        assertNull(name.getError());
        assertNull(email.getError());

        Thread.sleep(2000);
            assertEquals("email@testes.com", session.getUserLogged().getEmail());
            assertEquals("Name", session.getUserLogged().getName());

        Thread.sleep(5000);
            myActivityTestRule.launchActivity(it);
            onView(withId(R.id.edit_text_email)).perform(clearText(),
                    typeText(afterEmail), closeSoftKeyboard());
            onView(withId(R.id.edit_text_name)).perform(clearText(), typeText(
                    afterName), closeSoftKeyboard());
            onView(withId(R.id.action_save)).perform(click());
    }


}
