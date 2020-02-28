package br.edu.uepb.nutes.haniot.activity.account;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.ErrorHandler;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.service.TokenExpirationService;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static br.edu.uepb.nutes.haniot.data.model.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.UserType.HEALTH_PROFESSIONAL;
import static br.edu.uepb.nutes.haniot.data.model.UserType.PATIENT;

/**
 * LoginActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "LoginActivity";

    @BindView(R.id.progressBarLogin)
    ProgressBar mProgressBar;

    @BindView(R.id.alert_error_connectivity)
    FrameLayout mAlertConnectivity;

    @BindView(R.id.edit_text_email)
    EditText emailEditText;

    @BindView(R.id.btn_forgot)
    Button buttonForgot;

    @BindView(R.id.btn_ok)
    Button buttonOk;

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.box_response)
    LinearLayout boxResponse;

    @BindView(R.id.box_forgout)
    LinearLayout boxForgout;

    @BindView(R.id.message_error)
    TextView messageError;

    private TokenExpirationService tokenExpirationService;
    private boolean mIsBound;
    private DeviceDAO mDeviceDAO;
    private HaniotNetRepository haniotNetRepository;
    private CompositeDisposable compositeDisposable;
    private AppPreferencesHelper appPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorAccent));
        }
        mDeviceDAO = DeviceDAO.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        compositeDisposable = new CompositeDisposable();
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);

        doBindService();

        buttonForgot.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
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
        compositeDisposable.dispose();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot:
                login();
                break;
            case R.id.btn_ok:
                finish();
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

        forgoutPassword();
    }

    private void showSucess() {
        boxResponse.setVisibility(View.VISIBLE);
        boxForgout.setVisibility(View.GONE);
    }

    /**
     * Authenticates the user on the remote server
     */
    private void forgoutPassword() {
//        user.set
        JsonObject email = new JsonObject();
        email.addProperty("email", emailEditText.getText().toString());
        compositeDisposable.add(haniotNetRepository
                .forgotPassword(email)
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> showLoading(false))
                .subscribe(o -> {
                    showSucess();
                }, this::errorHandler)
        );
    }

    /**
     * Get data user from server.
     *
     * @param userAccess {@link UserAccess}
     */
    private void getUserProfile(UserAccess userAccess) {
        switch (userAccess.getTokenType()) {
            case HEALTH_PROFESSIONAL:
                compositeDisposable.add(haniotNetRepository
                        .getHealthProfissional(userAccess.getSubject())
                        .doOnSubscribe(disposable -> showLoading(true))
                        .doAfterTerminate(() -> showLoading(false))
                        .subscribe(user -> {
                            if (user.get_id() == null) {
                                showMessage(R.string.error_recover_data);
                                showLoading(false);
                                return;
                            }
                            user.setUserType(HEALTH_PROFESSIONAL);
                            saveUserInfo(user);
                        }, this::errorHandler)
                );
                break;
            case ADMIN:
                compositeDisposable.add(haniotNetRepository
                        .getAdmin(userAccess.getSubject())
                        .doOnSubscribe(disposable -> showLoading(true))
                        .doAfterTerminate(() -> showLoading(false))
                        .subscribe(user -> {
                            if (user.get_id() == null) {
                                showMessage(R.string.error_recover_data);
                                showLoading(false);
                                return;
                            }
                            user.setUserType(ADMIN);
                            saveUserInfo(user);
                        }, this::errorHandler)
                );
                break;
            case PATIENT:
                compositeDisposable.add(haniotNetRepository
                        .getPatient(userAccess.getSubject())
                        .doOnSubscribe(disposable -> showLoading(true))
                        .doAfterTerminate(() -> showLoading(false))
                        .subscribe(user -> {
                            if (user.get_id() == null) {
                                showMessage(R.string.error_recover_data);
                                showLoading(false);
                                return;
                            }
                            user.setUserType(PATIENT);
                            saveUserInfo(user);
                            appPreferencesHelper.saveLastPatient(user);
                        }, this::errorHandler)
                );
                break;
        }
    }

    /**
     * Save user info in AppPreferences.
     *
     * @param user
     */
    private void saveUserInfo(User user) {
        appPreferencesHelper.saveUserLogged(user);
        tokenExpirationService.initTokenMonitor();
        syncDevices(user.get_id());
    }

    /**
     * Get devices saved on the server and sync local.
     */
    public void syncDevices(String userId) {
        if (userId == null) return;

        compositeDisposable.add(haniotNetRepository
                .getAllDevices(userId)
                .doOnSubscribe(disposable -> showLoading(true))
                .doAfterTerminate(() -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
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
                    ForgotPasswordActivity.this,
                    ChangePasswordActivity.class
            );
            Log.i("AAA", redirectLink);
            appPreferencesHelper.saveString("user_id", redirectLink.split("/")[2]);
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
            Log.i(LOG_TAG, httpEx.message());
            switch (httpEx.code()) {
                case 401:
                    showMessage(R.string.validate_invalid_email_or_password);
                    break;
                case 429:
                    showMessage(R.string.error_limit_rate);
                    break;
                default:
                    ErrorHandler.showMessage(this, e);
                    break;
            }
            return;
        }
        Log.i(LOG_TAG, e.getMessage());
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

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.validate_email));
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        return true;
    }

    /**
     * Loading message,
     *
     * @param enabled boolean
     */
    private void showLoading(final boolean enabled) {
        runOnUiThread(() -> {
            buttonForgot.setEnabled(!enabled);

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
