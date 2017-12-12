package br.edu.uepb.nutes.haniot.activity.account;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.server.Server;
import br.edu.uepb.nutes.haniot.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;

/**
 * SignupActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, GenericDialogFragment.OnClickDialogListener {
    public final int DIALOG_HAS_CHANGE = 1;
    public static final String EXTRA_ID = "extra__id";

    private final String TAG = "SignupActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBarToolbar)
    ProgressBar mProgressBar;

    @BindView(R.id.box_signup)
    LinearLayout mBoxSignUp;

    @BindView(R.id.alert_error_connectivity)
    FrameLayout mAlertConnectivity;

    @BindView(R.id.edit_text_name)
    EditText nameEditText;

    @BindView(R.id.edit_text_date_of_birth)
    EditText dateOfBirthEditText;

    @BindView(R.id.edit_text_height)
    EditText heightEditText;

    @BindView(R.id.edit_text_email)
    EditText emailEditText;

    @BindView(R.id.edit_text_password)
    EditText passwordEditText;

    @BindView(R.id.edit_text_password_confirm)
    EditText passwordConfirmEditText;

    @BindView(R.id.radio_gender)
    RadioGroup genderRadioGroup;

    @BindView(R.id.radio_gender_male)
    RadioButton genderMaleRadioButton;

    @BindView(R.id.radio_gender_female)
    RadioButton genderFemaleRadioButton;

    @BindView(R.id.text_view_already_account)
    TextView alreadyAccountTextView;

    @BindView(R.id.text_input_layout_password)
    TextInputLayout passwordTextInputLayout;

    @BindView(R.id.text_input_layout_password_confirm)
    TextInputLayout passwordConfirmTextInputLayout;

    @BindView(R.id.btn_change_passsword)
    Button buttonChangePassword;

    private User user;
    private Session session;
    private UserDAO userDAO;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private Menu menu;
    private boolean isUpdate = false;
    private boolean openDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.new_account);

        alreadyAccountTextView.setOnClickListener(this);
        buttonChangePassword.setOnClickListener(this);

        dateOfBirthEditText.setOnClickListener(this);
        user = new User();
        session = new Session(this);
        userDAO = UserDAO.getInstance(this);
        calendar = Calendar.getInstance();

        /**
         * Check if it's to edit
         */
        Intent intent = getIntent();
        if (intent.getBooleanExtra(SettingsActivity.FORM_UPDATE, false)) {
            isUpdate = true;
            prepareEditing();

            heightEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
            heightEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) signup();

                    return false;
                }
            });
        } else {
            buttonChangePassword.setVisibility(View.GONE);

            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) signup();

                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options_toolbar, menu);
        this.menu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivity();
                break;
            case R.id.action_save:
                signup();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_already_account:
                closeActivity();
                break;
            case R.id.edit_text_date_of_birth:
                openDatePicker();
                break;
            case R.id.btn_change_passsword:
                if (user != null) {
                    Intent intent = new Intent(this, ChangePasswordActivity.class);
                    intent.putExtra(EXTRA_ID, user.get_id());
                    startActivity(intent);
                }
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        calendar.set(year, month, day, 0, 0, 0);

        user.setDateOfBirth(calendar.getTimeInMillis());
        dateOfBirthEditText.setText(DateUtils.calendarToString(calendar, getString(R.string.date_format)));
    }

    @Override
    public void onClickDialog(int id, int button) {
        if (id == DIALOG_HAS_CHANGE) {
            if (button == DialogInterface.BUTTON_POSITIVE) {
                super.onBackPressed();
            }
        }
    }

    /**
     * Open datepicker.
     */
    private void openDatePicker() {
        int year = calendar.get(Calendar.YEAR) - 20;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (user != null && user.getDateOfBirth() > 0) {
            int[] dateVelues = DateUtils.getDateValues(user.getDateOfBirth());
            year = dateVelues[0];
            month = dateVelues[1];
            day = dateVelues[2];
        }
        datePickerDialog = new DatePickerDialog(SignupActivity.this, this, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Prepare the view for editing the data
     */
    private void prepareEditing() {
        getSupportActionBar().setTitle(R.string.update_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alreadyAccountTextView.setOnClickListener(null);
        alreadyAccountTextView.setVisibility(View.GONE);
        passwordTextInputLayout.setVisibility(View.GONE);
        passwordConfirmTextInputLayout.setVisibility(View.GONE);
        buttonChangePassword.setVisibility(View.VISIBLE);
        emailEditText.setEnabled(false);
        emailEditText.setFocusable(false);
        emailEditText.setFocusableInTouchMode(false);

        enabledView(false);
        loading(true);

        // get user local
        user = userDAO.get(session.getIdLogged());

        if (!ConnectionUtils.internetIsEnabled(this)) {
            return;
        }
        populateView();

        /**
         * Get the required user token in request authentication
         */
        Headers headers = new Headers.Builder()
                .add("Authorization", "JWT ".concat(session.getTokenLogged()))
                .build();

        /**
         * Get user in server
         */
        Server.getInstance(this).get("users/", headers, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                enabledView(true);
                loading(false);
                printMessage(result);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    user = new Gson().fromJson(result.getString("user"), User.class);

                    if (user.get_id() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateView();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    enabledView(true);
                    loading(false);
                }
            }
        });
    }

    /**
     * Add user or update
     */
    private void signup() {
        /**
         * Check if you have an internet connection.
         * If yes, it sends the data to the remote server.
         */
        if (!checkConnectivity() || !validate())
            return;

        /**
         * Sign Up or Update
         */
        if (!isUpdate) {
            signUpInServer();
        } else {
            update();
        }
    }

    /**
     * Authenticates the user on the remote server
     */
    private void signUpInServer() {
        Log.i(TAG, "signup in remote server");
        loading(true);

        Server.getInstance(this).post("users/signup", new Gson().toJson(getUserView()), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                printMessage(result);
                loading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    final User user = new Gson().fromJson(result.getString("user"), User.class);
                    if (user.get_id() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userDAO.save(user); // save in local
                            }
                        });

                        finish(); // to back login activity
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading(false);
                    printMessage(result);
                }
            }
        });
    }

    /**
     * Update user in server
     */
    private void update() {
        Log.i(TAG, "update in remote server");
        if (user == null)
            return;

        loading(true);

        /**
         * Get the required user token in request authentication
         */
        Headers headers = new Headers.Builder()
                .add("Authorization", "JWT ".concat(session.getTokenLogged()))
                .build();

        Server.getInstance(this).put("users", new Gson().toJson(getUserView()), headers, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                printMessage(result);
                loading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    final User userUpdate = new Gson().fromJson(result.getString("user"), User.class);

                    if (userUpdate.get_id() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userDAO.update(user); // save in local
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading(false);
                    printMessage(result);
                }
            }
        });
    }

    /**
     * Displays return message to user
     *
     * @param response
     */
    private void printMessage(final JSONObject response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.has("code") && !response.has("unauthorized")) {
                        if (response.getInt("code") == 409) { // duplicate
                            Toast.makeText(getApplicationContext(), R.string.validate_register_user_not_duplicate, Toast.LENGTH_LONG).show();
                        } else if (!isUpdate && response.getInt("code") == 201) {
                            Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                        } else if (response.getInt("code") == 201) {
                            Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                        } else { // error 500
                            Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_LONG).show();
                        }
                    } else if (response.has("unauthorized")) {
                        Toast.makeText(getApplicationContext(), response.getString("unauthorized"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Populate elements of the view
     */
    private void populateView() {
        nameEditText.setText(user.getName());
        dateOfBirthEditText.setText(DateUtils.getDatetime(user.getDateOfBirth(), getString(R.string.date_format)));
        heightEditText.setText(String.valueOf(user.getHeight()));
        emailEditText.setText(user.getEmail());
        emailEditText.setEnabled(false);
        emailEditText.setFocusable(false);
        emailEditText.setFocusableInTouchMode(false);

        if (user.getGender() == 1) {
            genderMaleRadioButton.setChecked(true);
        } else {
            genderFemaleRadioButton.setChecked(true);
        }
    }

    /**
     * Validade form.
     * <p>
     * Note: "name" and "password" are not checked in the update
     *
     * @return boolean
     */
    public boolean validate() {
        openDialog = true;
        String name = nameEditText.getText().toString();
        String dateBirth = dateOfBirthEditText.getText().toString();
        String height = heightEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError(getString(R.string.validate_name));
            requestFocus(nameEditText);
            return false;
        } else {
            nameEditText.setError(null);
        }

        if (dateBirth.isEmpty()) {
            dateOfBirthEditText.setError(getString(R.string.validate_not_null));
            requestFocus(dateOfBirthEditText);
            return false;
        } else {
            dateOfBirthEditText.setError(null);
        }

        if (height.isEmpty() || Integer.valueOf(height) < 50 || Integer.valueOf(height) > 250) {
            heightEditText.setError(getString(R.string.validate_height));
            requestFocus(heightEditText);
            return false;
        } else {
            heightEditText.setError(null);
        }

        if (!isUpdate) {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError(getString(R.string.validate_email));
                requestFocus(emailEditText);
                return false;
            } else {
                emailEditText.setError(null);
            }
        }

        if (!isUpdate) {
            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                passwordEditText.setError(getString(R.string.validate_passoword));
                requestFocus(passwordEditText);
                return false;
            } else {
                passwordEditText.setError(null);
            }

            if (!password.isEmpty() && !password.equals(passwordConfirm)) {
                passwordConfirmEditText.setError(getString(R.string.validate_password_not_match_new));
                requestFocus(passwordConfirmEditText);
                return false;
            } else {
                passwordConfirmEditText.setError(null);
            }
        }

        return true;
    }

    /**
     * Opens the dialog to confirm that you really want to come back and rule changes.
     */
    private void closeActivity() {
        if (openDialog) {
            GenericDialogFragment dialogHasChange = GenericDialogFragment.newDialog(DIALOG_HAS_CHANGE,
                    R.string.back_confirm, new int[]{R.string.bt_ok, R.string.bt_cancel}, null);
            dialogHasChange.show(getSupportFragmentManager());
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Request focus in input
     *
     * @param editText
     */
    private void requestFocus(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * loading message
     *
     * @param enabled
     */
    private void loading(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menu != null) {
                    final MenuItem menuItem = menu.findItem(R.id.action_save);
                    menuItem.setEnabled(!enabled);
                }
                buttonChangePassword.setEnabled(!enabled);

                if (enabled) mProgressBar.setVisibility(View.VISIBLE);
                else mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Enable or disable view
     *
     * @param enabled
     */
    private void enabledView(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nameEditText.setEnabled(enabled);
                dateOfBirthEditText.setEnabled(enabled);
                heightEditText.setEnabled(enabled);
                buttonChangePassword.setEnabled(enabled);
                genderMaleRadioButton.setEnabled(enabled);
                genderFemaleRadioButton.setEnabled(enabled);
            }
        });
    }

    /**
     * Check if you have connectivity. If it does not, the elements in the view mounted to notify the user
     *
     * @return boolean
     */
    private boolean checkConnectivity() {
        if (!ConnectionUtils.internetIsEnabled(this)) {
            mAlertConnectivity.setVisibility(View.VISIBLE);
            return false;
        }
        mAlertConnectivity.setVisibility(View.GONE);

        return true;
    }

    /**
     * Retrieve the user data contained in the view.
     * <p>
     * Note: "dateOfBirth" is populated in the change event of datepicker
     *
     * @return User
     */
    private User getUserView() {
        user.setName(String.valueOf(nameEditText.getText()));
        user.setHeight(Integer.valueOf(heightEditText.getText().toString()));
        user.setGender(genderRadioGroup.getCheckedRadioButtonId() == R.id.radio_gender_male ? 1 : 2);
        user.setGroup(2); // common user

        if (!isUpdate) {
            user.setEmail(String.valueOf(emailEditText.getText()));
            user.setPassword(String.valueOf(passwordEditText.getText()));
        }

        return user;
    }
}
