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
import static junit.framework.Assert.assertTrue;

public class TEST06 {

    private EditText edit_current;
    private EditText edit_new;
    private EditText edit_confirm;
    private Button bt_save;
    private Context context;
    private Context contextChange;
    private String email = "testenutes@mail.com";
    private String passwordOld = "abc*123";
    private String passwordNew = "123*abc";
    private LoginActivity loginActivity;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityLogin = new ActivityTestRule<LoginActivity>
            (LoginActivity.class,true, true);
    @Rule
    public ActivityTestRule<ChangePasswordActivity> mActivityChange = new ActivityTestRule
            <ChangePasswordActivity>(ChangePasswordActivity.class, true,
            false);

    @Before
    public void setUp() throws Exception {
        loginActivity = mActivityLogin.getActivity();
        context = mActivityLogin.getActivity();
        ChangePasswordActivity changeActivity = mActivityChange.getActivity();
        edit_current = changeActivity.findViewById(R.id.edit_text_current_password);
        edit_new = changeActivity.findViewById(R.id.edit_text_new_password);
        edit_confirm = changeActivity.findViewById(R.id.edit_text_confirm_password);
    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void task01() throws InterruptedException {
        onView(withId(R.id.edit_text_email)).perform(typeText(
                email)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText(
                passwordOld)).perform(closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        Thread.sleep(1000);
            context.startActivity(new Intent(context , ChangePasswordActivity.class));

        onView(withId(R.id.edit_text_current_password)).perform(clearText(), typeText(passwordOld),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_new_password)).perform(clearText(), typeText(passwordNew),
                closeSoftKeyboard());
        onView(withId(R.id.edit_text_confirm_password)).perform(clearText(), typeText(passwordNew),
                closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());

        Thread.sleep(1000);
            contextChange.startActivity(new Intent(contextChange, LoginActivity.class));

        onView(withId(R.id.edit_text_email)).perform(typeText(
                email)).perform(closeSoftKeyboard());
        onView(withId(R.id.edit_text_password)).perform(typeText(
                passwordNew)).perform(closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        Thread.sleep(10000);
            Session session = new Session(context);
            assertTrue(session.isLogged());
            assertTrue(session.getUserLogged().getEmail().equals(email));

        Thread.sleep(1000);
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
