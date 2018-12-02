package br.edu.uepb.nutes.haniot.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ChangePasswordActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ChangePasswordActivity extends AppCompatActivity {
    private final String TAG = "ChangePasswordActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.alert_error_connectivity)
    FrameLayout mAlertConnectivity;

    @BindView(R.id.progressBarToolbar)
    ProgressBar mProgressBar;

    @BindView(R.id.edit_text_current_password)
    EditText currentPasswordEditText;

    @BindView(R.id.edit_text_new_password)
    EditText newPasswordEditText;

    @BindView(R.id.edit_text_confirm_password)
    EditText confirmPasswordEditText;

    private Menu menu;
    private Session session;
    private boolean isRedirect = false;
    private String pathRedirectLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        if (intent.hasExtra("pathRedirectLink")) {
            pathRedirectLink = intent.getStringExtra("pathRedirectLink");
            pathRedirectLink = pathRedirectLink.replace("/api/v1", "");
            isRedirect = true;
        }
        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND)
                    changePassword();

                return false;
            }
        });

        session = new Session(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_save:
                changePassword();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options_toolbar, menu);
        this.menu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Validade form
     *
     * @return boolean
     */
    public boolean validate() {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        /**
         * Regular expression to check if the password contains the default:
         *   - at least 1 number
         *   - At least 1 letter
         *   - At least 1 special character among the allowed: @#$%*<space>!?._+-
         *   - At least 6 characters
         */
        Pattern check1 = Pattern.compile("((?=.*[a-zA-Z0-9])(?=.*[@#$%* !?._+-]).{6,})");

        /**
         * Regular expression to check
         */
        Pattern check2 = Pattern.compile("([^a-zA-Z0-9@#$%&* !?._+-])");

        if (!check1.matcher(currentPassword).matches() || check2.matcher(currentPassword).find()) {
            currentPasswordEditText.setError(getString(R.string.validate_password));
            requestFocus(currentPasswordEditText);
            return false;
        } else {
            currentPasswordEditText.setError(null);
        }

        if (!check1.matcher(newPassword).matches() || check2.matcher(newPassword).find()) {
            newPasswordEditText.setError(getString(R.string.validate_password));
            requestFocus(newPasswordEditText);
            return false;
        } else {
            newPasswordEditText.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.validate_password_not_match_new));
            requestFocus(confirmPasswordEditText);
            return false;
        } else {
            confirmPasswordEditText.setError(null);
        }

        return true;
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
     * Change password
     */
    private void changePassword() {
        if (!checkConnectivity() || !validate())
            return;

        loadingSend(true);

        /**
         * Mount the object json
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_password", String.valueOf(currentPasswordEditText.getText()));
            jsonObject.put("new_password", String.valueOf(newPasswordEditText.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send for remote server /users/:userId/password
        if (!isRedirect) {
            Server.getInstance(this).patch("users/".concat(session.get_idLogged()) + "/password",
                    jsonObject.toString(), new Server.Callback() {
                        @Override
                        public void onError(JSONObject result) {
                            printMessage(result);
                            loadingSend(false);
                        }

                        @Override
                        public void onSuccess(JSONObject result) {
                            printMessage(result);
                            signOut(result);
                        }
                    });
        } else {
            Server.getInstance(this).patch(pathRedirectLink,
                    jsonObject.toString(), new Server.Callback() {
                        @Override
                        public void onError(JSONObject result) {
                            printMessage(result);
                            loadingSend(false);
                        }

                        @Override
                        public void onSuccess(JSONObject result) {
                            Log.i("Account", "Password changed for redirect");
                            printMessage(result);
                            signOut(result);
                        }
                    });
        }

    }

    private void signOut(JSONObject result) {
        /**
         * Remove user from session and redirect to login screen.
         */
        if (session.isLogged()) {
            session.removeLogged();
        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();

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
                        if (response.getInt("code") == 204) {
                            Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                        } else if (response.getInt("code") == 401) {
                            Toast.makeText(getApplicationContext(), R.string.validate_password_not_match_current, Toast.LENGTH_LONG).show();
                        } else if (response.getInt("code") == 400) {
                            Toast.makeText(getApplicationContext(), R.string.error_incorret_password, Toast.LENGTH_LONG).show();
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
     * loading message
     *
     * @param enabled
     */
    private void loadingSend(final boolean enabled) {
        final MenuItem menuItem = menu.findItem(R.id.action_save);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuItem.setEnabled(!enabled);

                if (enabled) mProgressBar.setVisibility(View.VISIBLE);
                else mProgressBar.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
