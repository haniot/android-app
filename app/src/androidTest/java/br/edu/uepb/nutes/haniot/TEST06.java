package br.edu.uepb.nutes.haniot;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.widget.Button;
import android.widget.EditText;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import br.edu.uepb.nutes.haniot.activity.account.ChangePasswordActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

public class TEST06 {

    private Context context;
    private Context contextChange;
    private String email = "testes@nutes.com";
    private String passwordOld = "testes*123";
    private String passwordNew = "123*testes";
    private LoginActivity loginActivity;
    private ChangePasswordActivity changeActivity;

    @Rule
    public ActivityTestRule<ChangePasswordActivity> mActivityChange = new ActivityTestRule
            <ChangePasswordActivity>(ChangePasswordActivity.class, true,
            true);
    @Rule
    public ActivityTestRule<LoginActivity> mActivityLogin = new ActivityTestRule<LoginActivity>
            (LoginActivity.class,true, true);

    @Before
    public void setUp() throws Exception {
         changeActivity = mActivityChange.getActivity();
         contextChange = changeActivity.getApplicationContext();
         loginActivity = mActivityLogin.getActivity();
         context = loginActivity.getApplicationContext();
    }

    @After
    public void tearDown() throws Exception {
        
    }

    /**
     * Test if is possible change password
     * **/

    @Test
    public void task01() throws InterruptedException {
        Thread.sleep(2000);
            contextChange.startActivity(new Intent(contextChange, LoginActivity.class));
            onView(withId(R.id.edit_text_email)).perform(typeText(
                    email)).perform(closeSoftKeyboard());
            onView(withId(R.id.edit_text_password)).perform(typeText(
                    passwordOld)).perform(closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());

        Thread.sleep(2000);
            context.startActivity(new Intent(context , ChangePasswordActivity.class));

        onView(withId(R.id.edit_text_current_password)).perform(clearText(), typeText(passwordOld),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_new_password)).perform(clearText(), typeText(passwordNew),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_confirm_password)).perform(clearText(), typeText(passwordNew),
                closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());

        Thread.sleep(2000);
            contextChange.startActivity(new Intent(contextChange, LoginActivity.class));

        onView(withId(R.id.edit_text_email)).perform(typeText(
                email)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText(
                passwordNew)).perform(closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        Thread.sleep(10000);
            Session session = new Session(context);
            assertNotNull(session.isLogged());

        Thread.sleep(2000);
        context.startActivity(new Intent(context , ChangePasswordActivity.class));

        onView(withId(R.id.edit_text_current_password)).perform(clearText(), typeText(passwordNew),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_new_password)).perform(clearText(), typeText(passwordOld),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_confirm_password)).perform(clearText(), typeText(passwordOld),
                closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
    }
}
