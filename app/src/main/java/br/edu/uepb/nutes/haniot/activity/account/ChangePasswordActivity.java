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

    private User user;
    private Menu menu;
    private Session session;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND)
                    changePassword();

                return false;
            }
        });

        session = new Session(this);
        userDAO = UserDAO.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String uuid = intent.getStringExtra(SignupActivity.EXTRA_ID);

        user = uuid != null ? userDAO.get(uuid) : null;
        if (user == null) {
            Toast.makeText(getApplicationContext(), R.string.error_connectivity, Toast.LENGTH_LONG).show();
            finish();
        }
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

        if (currentPassword.isEmpty() || currentPassword.length() < 4 || currentPassword.length() > 10) {
            currentPasswordEditText.setError(getString(R.string.validate_passoword));
            requestFocus(currentPasswordEditText);
            return false;
        } else {
            currentPasswordEditText.setError(null);
        }

        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            newPasswordEditText.setError(getString(R.string.validate_passoword));
            requestFocus(newPasswordEditText);
            return false;
        } else {
            currentPasswordEditText.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            confirmPasswordEditText.setError(getString(R.string.validate_passoword));
            requestFocus(confirmPasswordEditText);
            return false;
        } else if (!newPassword.isEmpty() && !confirmPassword.equals(newPassword)) {
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
        if (!checkConnectivity() || !validate() || user == null)
            return;

        loadingSend(true);

        /**
         * Mount the object json
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("currentPassword", String.valueOf(currentPasswordEditText.getText()));
            jsonObject.put("newPassword", String.valueOf(newPasswordEditText.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send for remote server /users/:userId/password
        Server.getInstance(this).put("users/".concat(session.get_idLogged()) + "/password",
                jsonObject.toString(), new Server.Callback() {
                    @Override
                    public void onError(JSONObject result) {
                        printMessage(result);
                        loadingSend(false);
                    }

                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            final User userUpdate = new Gson().fromJson(result.getString("user"), User.class);
                            Log.i("USER UPDATE", userUpdate.toString());

                            if (userUpdate.getPassword() != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        userDAO.update(userUpdate); // save in local
                                    }
                                });

                                /**
                                 * Remove user from session and redirect to login screen.
                                 */
                                session.removeLogged();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            loadingSend(false);
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
                        if (response.getInt("code") == 201) {
                            Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                        } else if (response.getInt("code") == 401) {
                            Toast.makeText(getApplicationContext(), R.string.validate_password_not_match_current, Toast.LENGTH_LONG).show();
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
}
