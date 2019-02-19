package br.edu.uepb.nutes.haniot.activity.account;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.service.TokenExpirationService;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * LoginActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>, Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
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

    private Session session;
    private UserDAO userDAO;
    private TokenExpirationService tokenExpirationService;
    private boolean mIsBound;
    private Server server;
    private DeviceDAO mDeviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        doBindService();
        session = new Session(this);
        userDAO = UserDAO.getInstance(this);
        mDeviceDAO = DeviceDAO.getInstance(this);
        server = Server.getInstance(this);

        buttonLogin.setOnClickListener(this);

        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) login();

            return false;
        });
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
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
        Server.getInstance(this).post("users/auth", getJsonData(), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                printMessage(result);
                loadingSend(false);
                try {
                    if (result.getString("code").equals("403")) {
                        Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                        intent.putExtra("pathRedirectLink", result.get("redirect_link").toString());
                        startActivity(intent);
                    } else {
                        Log.i("CACA", "error 404");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    User user = new User();
                    final String token = result.has("token") ? new Gson().fromJson(result.getString("token"), String.class) : null;
                    if (token != null) {
                        JWT jwt = new JWT(token);
                        String _id = jwt.getSubject();
                        user.set_id(_id);
                        user.setToken(token);

                        User u = userDAO.get(user.get_id());
                        if (u != null) {
                            user.setIdDb(u.getIdDb());
                            userDAO.update(user);
                        } else {
                            userDAO.save(user);
                            user = userDAO.get(user.get_id());
                        }
                        session.setLogged(user.getIdDb(), token);

                        // FCM TOKEN
                        String fcmToken = FirebaseInstanceId.getInstance().getToken();
                        if (fcmToken != null) {
                            user.setToken(fcmToken);
                            sendFcmToken(fcmToken);
                        }
                        tokenExpirationService.initTokenMonitor();
                        syncDevices();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                    } else if (response.has("code") && response.getInt("code") == 403) {
                        Toast.makeText(getApplicationContext(), R.string.error_403, Toast.LENGTH_LONG).show();
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
        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.required_field));
            valid = false;
            if (emailEditText.getError() == null) passwordEditText.requestFocus();
        } else {
            passwordEditText.setError(null);
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
        Log.i("CACA", dataJson.toString());
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

    /**
     * Bind Account Service.
     */
    public void doBindService() {
        bindService(new Intent(this, TokenExpirationService.class),
                mServiceConnection,
                BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Unbind Account Service.
     */
    public void doUnbindService() {
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    /**
     * Code to manage Service lifecycle.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            tokenExpirationService = ((TokenExpirationService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            tokenExpirationService = null;
        }
    };

    /**
     * Deserialize json in a list of devices.
     * If any error occurs it will be returned List empty.
     *
     * @param json {@link JSONObject}
     * @return {@link List<Device>}
     */
    private List<Device> jsonToListDevice(JSONObject json) {
        if (json == null || !json.has("devices")) return new ArrayList<>();

        Type typeUserAccess = new TypeToken<List<Device>>() {
        }.getType();

        try {
            JSONArray jsonArray = json.getJSONArray("devices");
            return new Gson().fromJson(jsonArray.toString(), typeUserAccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //check devices saved on the server
    public void syncDevices() {
        String path = "devices/users/".concat(session.get_idLogged());
        server.get(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
            }

            @Override
            public void onSuccess(JSONObject result) {
                List<Device> devicesRegistered = jsonToListDevice(result);
                mDeviceDAO.removeAll(session.getUserLogged().getIdDb());
                if (!devicesRegistered.isEmpty()) {
                    mDeviceDAO.removeAll(session.getUserLogged().getIdDb());
                    for (Device d : devicesRegistered) {
                        d.setUser(session.getUserLogged());
                        mDeviceDAO.save(d);
                    }
                }
            }
        });
    }
}
