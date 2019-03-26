package br.edu.uepb.nutes.haniot.activity.account;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.service.TokenExpirationService;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * LoginActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "LoginActivity";

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

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    private TokenExpirationService tokenExpirationService;
    private boolean mIsBound;
    private DeviceDAO mDeviceDAO;
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mDeviceDAO = DeviceDAO.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);

        doBindService();

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
        boxMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        DisposableManager.dispose();
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
        boxMessage.setVisibility(View.GONE);

        // close keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(
                getCurrentFocus()).getWindowToken(), HIDE_NOT_ALWAYS);

        // Check if you have an internet connection.
        // If yes, it does authentication with the remote server
        if (!checkConnectivity() || !validate()) return;

        authenticationInServer();
    }

    /**
     * Authenticates the user on the remote server
     */
    private void authenticationInServer() {
        DisposableManager.add(haniotNetRepository
                .auth(String.valueOf(emailEditText.getText()), String.valueOf(passwordEditText.getText()))
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> showLoading(false))
                .subscribe(userAccess -> {
                    if (appPreferencesHelper.saveUserAccessHaniot(userAccess)) {
                        getUserProfile(userAccess.getSubject());
                    }
                }, this::errorHandler)
        );
    }

    /**
     * Get data user from server.
     *
     * @param userId {@link String}
     */
    private void getUserProfile(String userId) {
        DisposableManager.add(haniotNetRepository
                .getHealthProfissional(userId)
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> showLoading(false))
                .subscribe(user -> {
                    if (user.get_id() == null) {
                        showMessage(R.string.error_recover_data);
                        showLoading(false);
                        return;
                    }

                    appPreferencesHelper.saveUserLogged(user);
                    tokenExpirationService.initTokenMonitor();
                    syncDevices(userId);
                }, this::errorHandler)
        );
    }

    /**
     * Get devices saved on the server and sync local.
     */
    public void syncDevices(String userId) {
        if (userId == null) return;

        DisposableManager.add(haniotNetRepository
                .getAllDevices(userId)
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> startActivity(new Intent(this, MainActivity.class)))
                .subscribe(devices -> {
                    mDeviceDAO.removeAll(userId);
                    for (Device d : devices) {
                        d.setUserId(userId);
                        mDeviceDAO.save(d);
                    }
                }, error -> Log.w(LOG_TAG, "syncDevices() error: " + error.getMessage()))
        );
    }

    /**
     * Open screen to change password.
     *
     * @param body {@link ResponseBody}
     */
    private void openScreenChangePassword(ResponseBody body) {
        try {
            JsonObject json = new JsonParser()
                    .parse(body.string())
                    .getAsJsonObject();
            String redirectLink = json.get("redirect_link").getAsString();
            Intent intent = new Intent(
                    LoginActivity.this,
                    ChangePasswordActivity.class
            );
            intent.putExtra("user_id", redirectLink.split("/")[2]);
            startActivity(intent);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);

            switch (httpEx.code()) {
                case 401:
                    showMessage(R.string.validate_invalid_email_or_password);
                    break;
                case 403: {
                    if (httpEx.response().errorBody() == null) {
                        showMessage(R.string.error_500);
                        return;
                    }
                    openScreenChangePassword(Objects.requireNonNull(httpEx.response().errorBody()));
                    break;
                }
                case 404:
                    showMessage(R.string.error_recover_data);
                    break;
                default:
                    showMessage(R.string.error_500);
                    break;
            }
            return;
        }
        showMessage(R.string.error_500);
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    private void showMessage(@StringRes int str) {
        String message = getString(str);
        if (message.isEmpty()) message = getString(R.string.error_500);

        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Validate form.
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
     * Loading message,
     *
     * @param enabled boolean
     */
    private void showLoading(final boolean enabled) {
        runOnUiThread(() -> {
            buttonLogin.setEnabled(!enabled);

            if (enabled) mProgressBar.setVisibility(View.VISIBLE);
            else mProgressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Check if you have connectivity.
     * If it does not, the elements in the view mounted to notify the user.
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
                mServiceConnection, BIND_AUTO_CREATE);
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
}
