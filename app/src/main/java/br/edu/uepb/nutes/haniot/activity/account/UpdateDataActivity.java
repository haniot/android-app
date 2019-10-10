package br.edu.uepb.nutes.haniot.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.ErrorHandler;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.type.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.HEALTH_PROFESSIONAL;
import static br.edu.uepb.nutes.haniot.data.model.type.UserType.PATIENT;

/**
 * UpdateDataActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "UpdateDataActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBarToolbar)
    ProgressBar mProgressBar;

    @BindView(R.id.alert_error_connectivity)
    FrameLayout mAlertConnectivity;

    @BindView(R.id.edit_text_name)
    EditText nameEditText;

    @BindView(R.id.edit_text_email)
    EditText emailEditText;

    @BindView(R.id.btn_change_passsword)
    Button buttonChangePassword;

    private User user;
    private AppPreferencesHelper appPreferences;
    private HaniotNetRepository haniotNetRepository;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_data);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.update_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonChangePassword.setOnClickListener(this);

        appPreferences = AppPreferencesHelper.getInstance(this);
        haniotNetRepository = HaniotNetRepository.getInstance(this);

        user = appPreferences.getUserLogged();
        if (user == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
        prepareEditing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options_toolbar, menu);
        this.menu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_save:
                update();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_passsword:
                if (user != null) {
                    Intent intent = new Intent(this, ChangePasswordActivity.class);
                    startActivity(intent);
                }
            default:
                break;
        }
    }

    /**
     * Prepare the view for editing the data
     */
    private void prepareEditing() {
        switch (appPreferences.getUserLogged().getUserType()) {
            case ADMIN:
                DisposableManager.add(haniotNetRepository
                        .getAdmin(user.get_id())
                        .doOnSubscribe(disposable -> {
                            populateView(); // Populate view with local data
                            enabledView(false);
                            loading(true);
                        })
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(admin -> {
                            if (admin.getEmail() != null) user.setEmail(admin.getEmail());
                            if (admin.getName() != null) user.setName(admin.getName());

                            populateView();
                            enabledView(true);
                        }, this::errorHandler));
                break;
            case HEALTH_PROFESSIONAL:
                DisposableManager.add(haniotNetRepository
                        .getHealthProfissional(user.get_id())
                        .doOnSubscribe(disposable -> {
                            populateView(); // Populate view with local data
                            enabledView(false);
                            loading(true);
                        })
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(healthP -> {
                            if (healthP.getEmail() != null) user.setEmail(healthP.getEmail());
                            if (healthP.getName() != null) user.setName(healthP.getName());

                            populateView();
                            enabledView(true);
                        }, this::errorHandler));
                break;
            case PATIENT:
                DisposableManager.add(haniotNetRepository
                        .getPatient(user.get_id())
                        .doOnSubscribe(disposable -> {
                            populateView(); // Populate view with local data
                            enabledView(false);
                            loading(true);
                        })
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(patient -> {
                            if (patient.getEmail() != null) user.setEmail(patient.getEmail());
                            if (patient.getName() != null) user.setName(patient.getName());

                            populateView();
                            enabledView(true);
                        }, this::errorHandler));
                break;
        }
    }

    /**
     * Add user or updateInServer
     */
    private void update() {
        // Check if you have an internet connection.
        // If yes, it sends the data to the remote server.
        if (!checkConnectivity() || !validate()) return;
        updateInServer();
    }

    /**
     * Update user in server
     */
    private void updateInServer() {
        if (user == null) return;

        switch (appPreferences.getUserLogged().getUserType()) {
            case ADMIN:
                DisposableManager.add(haniotNetRepository
                        .updateAdmin((Admin) getUserView())
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(healthP -> {
                            if (healthP.getEmail() != null) user.setEmail(healthP.getEmail());
                            if (healthP.getName() != null) user.setName(healthP.getName());

                            appPreferences.saveUserLogged(user);
                            enabledView(true);
                            showMessage(200);
                        }, this::errorHandler)
                );
                break;
            case HEALTH_PROFESSIONAL:
                DisposableManager.add(haniotNetRepository
                        .updateHealthProfissional((HealthProfessional) getUserView())
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(admin -> {
                            if (admin.getEmail() != null) user.setEmail(admin.getEmail());
                            if (admin.getName() != null) user.setName(admin.getName());

                            appPreferences.saveUserLogged(user);
                            enabledView(true);
                            showMessage(200);
                        }, this::errorHandler)
                );
                break;
            case PATIENT:
                DisposableManager.add(haniotNetRepository
                        .updatePatient((Patient) getUserView())
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(patient -> {
                            if (patient.getEmail() != null) user.setEmail(patient.getEmail());
                            if (patient.getName() != null) user.setName(patient.getName());

                            appPreferences.saveUserLogged(user);
                            enabledView(true);
                            showMessage(200);
                        }, this::errorHandler)
                );
                break;
        }
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        ErrorHandler.showMessage(this, e);
    }

    /**
     * Displays return message.
     *
     * @param code int
     */
    private void showMessage(final int code) {
        runOnUiThread(() -> {
            switch (code) {
                case 200:
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_LONG).show();
                    break;
                case 404:
                    Toast.makeText(this, R.string.error_recover_data, Toast.LENGTH_LONG).show();
                    break;
                case 409:
                    Toast.makeText(this, R.string.validate_register_user_not_duplicate,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, R.string.error_500, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Populate elements of the view
     */
    private void populateView() {
        if (user == null) {
            finish();
            return;
        }
        if (user.getName() != null) nameEditText.setText(user.getName());
        if (user.getEmail() != null) emailEditText.setText(user.getEmail());
    }

    /**
     * Validate form.
     *
     * @return boolean
     */
    public boolean validate() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError(getString(R.string.validate_name));
            requestFocus(nameEditText);
            return false;
        } else {
            nameEditText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.validate_email));
            requestFocus(emailEditText);
            return false;
        } else {
            emailEditText.setError(null);
        }

        return true;
    }

    /**
     * Request focus in input
     *
     * @param editText {@link EditText}
     */
    private void requestFocus(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * loading message
     *
     * @param enabled boolean
     */
    private void loading(final boolean enabled) {
        runOnUiThread(() -> {
            if (menu != null) {
                final MenuItem menuItem = menu.findItem(R.id.action_save);
                menuItem.setEnabled(!enabled);
            }
            buttonChangePassword.setEnabled(!enabled);
            if (enabled) mProgressBar.setVisibility(View.VISIBLE);
            else mProgressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Enable or disable view
     *
     * @param enabled boolean
     */
    private void enabledView(final boolean enabled) {
        runOnUiThread(() -> {
            nameEditText.setEnabled(enabled);
            emailEditText.setEnabled(enabled);
            buttonChangePassword.setEnabled(enabled);

        });
    }

    /**
     * Check if you have connectivity.
     * If it does not, the elements in the view mounted to notify the user
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
     *
     * @return User
     */
    private User getUserView() {
        user.setName(String.valueOf(nameEditText.getText()));
        user.setEmail(String.valueOf(emailEditText.getText()));

        return user;
    }
}
