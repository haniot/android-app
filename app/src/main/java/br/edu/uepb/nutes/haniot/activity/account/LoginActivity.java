package br.edu.uepb.nutes.haniot.activity.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * LoginActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "LoginActivity";


    @BindView(R.id.progressBarLogin)
    ProgressBar mProgressBar;

    @BindView(R.id.alert_error_connectivity)
    FrameLayout mAlertConnectivity;

    @BindView(R.id.edit_text_email)
    EditText emailEditText;

    @BindView(R.id.edit_text_password)
    EditText passwordEditText;

    @BindView(R.id.btn_login)
    Button buttonLogin;

    @BindView(R.id.text_view_signup)
    TextView signupTextView;

    private Session session;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        session = new Session(this);
        userDAO = UserDAO.getInstance(this);

        signupTextView.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        /**
         * Checks if user is in session and redirect to main screen.
         */
        if (session.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                break;
            case R.id.btn_login:
                login();
                break;
            default:
                break;
        }
    }

    /**
     * Authenticate user
     */
    private void login() {
        // close keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide

        /**
         * Check if you have an internet connection.
         * If yes, it does authentication with the remote server
         */
        if (!checkConnectivity() || !validate())
            return;

        authenticationInServer();
    }

    /**
     * Authenticates the user on the remote server
     */
    private void authenticationInServer() {
        Log.i(TAG, "authenticationInServer()");
        loadingSend(true);

        // Send for remote server /users/authenticate
        Server.getInstance(this).post("users/authenticate", getJsonData(), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                printMessage(result);
                loadingSend(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    User user = result.has("user") ? new Gson().fromJson(result.getString("user"), User.class) : null;
                    final String token = result.has("token") ? new Gson().fromJson(result.getString("token"), String.class) : null;

                    if (user.get_id() != null && token != null) {
                        user.setToken(token);
                        User u = userDAO.get(user.getEmail());

                        if (u != null) {
                            userDAO.update(user);
                        } else {
                            userDAO.save(user);
                            user = userDAO.get(user.getEmail());
                        }
                        session.setLogged(user.getId(), token);

                        // FCM TOKEN
                        String fcmToken = FirebaseInstanceId.getInstance().getToken();
                        if (fcmToken != null) {
                            user.setToken(fcmToken);
                            sendFcmToken(fcmToken);
                        }

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        printMessage(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loadingSend(false);
                }
            }
        });
    }

    /**
     * Send token in FCM.
     *
     * @param fcmToken
     */
    private void sendFcmToken(String fcmToken) {
        Log.d(TAG, "FCM_TOKEN:" + fcmToken);

        String path = "users/".concat(session.get_idLogged()).concat("/fcm");
        String jsonToken = "{\"fcmToken\":\"".concat(fcmToken).concat("\"}");

        Server.getInstance(this).post(path, jsonToken, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                Log.d(TAG, result.toString());
            }

            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, result.toString());
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
                    if (response.has("code") && response.getInt("code") == 401) {
                        Toast.makeText(getApplicationContext(), R.string.validate_invalid_email_or_password, Toast.LENGTH_LONG).show();
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
     * Validade form
     *
     * @return boolean
     */
    public boolean validate() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.validate_email));
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        return valid;
    }

    /**
     * Loading message
     *
     * @param enabled
     */
    private void loadingSend(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonLogin.setEnabled(!enabled);

                if (enabled) mProgressBar.setVisibility(View.VISIBLE);
                else mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Return json with the data typed in the form
     *
     * @return String json format
     */
    private String getJsonData() {
        final JSONObject dataJson = new JSONObject();

        try {
            dataJson.put("email", String.valueOf(emailEditText.getText()));
            dataJson.put("password", String.valueOf(passwordEditText.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataJson.toString();
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
