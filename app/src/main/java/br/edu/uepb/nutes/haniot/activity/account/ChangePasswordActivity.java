package br.edu.uepb.nutes.haniot.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.regex.Pattern;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

/**
 * ChangePasswordActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ChangePasswordActivity extends AppCompatActivity {
    private final String LOG_TAG = "ChangePasswordActivity";

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
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferences;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferences = AppPreferencesHelper.getInstance(this);

        Intent intent = getIntent();
        if (intent.hasExtra("user_id")) { // From redirect link
            user = new User();
            user.set_id(appPreferences.getString("user_id"));
        } else {
            user = appPreferences.getUserLogged();
        }

        confirmPasswordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) changePassword();
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
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
     * Validate form
     *
     * @return boolean
     */
    public boolean validate() {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Regular expression to check if the password contains the default:
        //   - at least 1 number
        //   - At least 1 letter
        //   - At least 1 special character among the allowed: @#$%*<space>!?._+-
        //   - At least 6 characters
        Pattern check1 = Pattern.compile("((?=.*[a-zA-Z0-9])(?=.*[@#$%* !?._+-]).{6,})");

        // Regular expression to check
        Pattern check2 = Pattern.compile("([^a-zA-Z0-9@#$%&* !?._+-])");

        if (currentPassword.isEmpty()) {
            currentPasswordEditText.setError(getString(R.string.validate_not_null));
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
     * @param editText {@link EditText}
     */
    private void requestFocus(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Change password
     */
    private void changePassword() {
        if (!checkConnectivity() || !validate()) return;

        user.setOldPassword(String.valueOf(currentPasswordEditText.getText()));
        user.setNewPassword(String.valueOf(newPasswordEditText.getText()));

        DisposableManager.add(haniotNetRepository
                .changePassword(user)
                .doOnSubscribe(disposable -> loadingSend(true))
                .doAfterTerminate(() -> loadingSend(false))
                .subscribe(() -> {
                    printMessage(204);
                    signOut();
                }, this::errorHandler)
        );
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
            printMessage(httpEx.code());
            return;
        }
        printMessage(500);
    }

    /**
     * Remove user from session and redirect to login screen.
     */
    private void signOut() {
        appPreferences.removeUserLogged();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    /**
     * Displays return message to user
     */
    private void printMessage(final int code) {
        runOnUiThread(() -> {
            switch (code) {
                case 204:
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_LONG).show();
                    break;
                case 400:
                    Toast.makeText(this, R.string.validate_password_not_match_current,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, R.string.error_500, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * loading message
     *
     * @param enabled boolean
     */
    private void loadingSend(final boolean enabled) {
        final MenuItem menuItem = menu.findItem(R.id.action_save);

        runOnUiThread(() -> {
            menuItem.setEnabled(!enabled);

            if (enabled) mProgressBar.setVisibility(View.VISIBLE);
            else mProgressBar.setVisibility(View.GONE);
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
