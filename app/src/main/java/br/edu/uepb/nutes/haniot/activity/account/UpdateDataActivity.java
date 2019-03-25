package br.edu.uepb.nutes.haniot.activity.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UpdateDataActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>,
 * Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>,
 * Arthur Stevam <arthurstevam.ac@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener, GenericDialogFragment.OnClickDialogListener {
    public final int DIALOG_HAS_CHANGE = 1;

    private final String TAG = "UpdateDataActivity";

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
    private UserDAO userDAO;
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
        userDAO = UserDAO.getInstance(this);
        user = appPreferences.getUserLogged();

        prepareEditing();
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

    @Override
    public void onClickDialog(int id, int button) {
        if (id == DIALOG_HAS_CHANGE) {
            if (button == DialogInterface.BUTTON_POSITIVE)
                super.onBackPressed();
        }
    }

    /**
     * Prepare the view for editing the data
     */
    private void prepareEditing() {
        enabledView(false);

        if (!ConnectionUtils.internetIsEnabled(this)) return;

        loading(true);
        populateView(); // Populate view with local data

        haniotNetRepository.getHealthProfissional(user.get_id())
                .doOnSubscribe(disposable -> {
                    loading(true);
                    populateView();
                })
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        if (user.getEmail() != null) {
                            runOnUiThread(() -> {
                                populateView(); // Populate view with data from server
                                enabledView(true);
                                loading(false);
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        enabledView(true);
                        loading(false);
                        if (e instanceof HttpException) {
                            HttpException httpEx = ((HttpException) e);
                            printMessage(httpEx);
                        }
                        //printMessage(result);
                    }
                });
    }

    /**
     * Add user or updateInServer
     */
    private void update() {
        /**
         * Check if you have an internet connection.
         * If yes, it sends the data to the remote server.
         */
        if (!checkConnectivity() || !validate())
            return;

        updateInServer();
    }

    /**
     * Update user in server
     */
    private void updateInServer() {
        if (user == null)
            return;

        haniotNetRepository.updateHealthProfissional(getUserView())
                .doOnSubscribe(disposable -> loading(true))
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User userUpdate) {
                        if (userUpdate.getEmail() != null)
                            userDAO.update(user); // save in local
                        Log.i("Account", "Updated: " + userDAO.get(userUpdate.get_id()).toString());
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException httpEx = ((HttpException) e);
                            printMessage(httpEx);
                        }
                        loading(false);
                    }
                });
    }

    /**
     * Displays return message to user
     *
     * @param
     */
    private void printMessage(final HttpException httpEx) {
        runOnUiThread(() -> {
            if (httpEx.code() == 409) { // duplicate
                Toast.makeText(getApplicationContext(), R.string.validate_register_user_not_duplicate, Toast.LENGTH_LONG).show();
            } else if (httpEx.code() == 200) {
                Toast.makeText(getApplicationContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
            } else { // error 500
                Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_LONG).show();
            }
            if (httpEx.message().contains("unauthorized")) {
                Toast.makeText(getApplicationContext(), httpEx.message(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_LONG).show();
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
     * Validade form.
     * Note: "email" is not validated in the updateInServer. Because it should not be updated.
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
     * Opens the dialog to confirm that you really want to come back and rule changes.
     */
    private void closeActivity() {
        super.onBackPressed();
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
     * @param enabled
     */
    private void enabledView(final boolean enabled) {
        runOnUiThread(() -> {
            nameEditText.setEnabled(enabled);
            emailEditText.setEnabled(enabled);
            buttonChangePassword.setEnabled(enabled);
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
     *
     * @return User
     */
    private User getUserView() {
        user.setName(String.valueOf(nameEditText.getText()));
        user.setEmail(String.valueOf(emailEditText.getText()));

        return user;
    }
}
