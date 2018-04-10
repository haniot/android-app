package br.edu.uepb.nutes.haniot.elderly;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Elderly;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ElderlyRegisterActivity extends AppCompatActivity implements ElderlyPinFragment.OnNextPageSelectedListener {
    private final String TAG = "ElderlyRegisterActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_register);
        ButterKnife.bind(this);

        initComponents();
    }

    private void initComponents() {
        initToolBar();
        openFragment(ElderlyPinFragment.newInstance());
    }

    /**
     * Open fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof ElderlyFormFragment)
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.content_form_elderly, fragment).commit();
//        transaction.replace(R.id.content_form_elderly, fragment).addToBackStack(null).commit();
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.elderly_add));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.colorPrimarySecondary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,
                    R.color.colorPrimaryDarkSecondary));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_save:
                save();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (!validate()) return;

        loading(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

//        String path = "users/".concat(session.get_idLogged()).concat("/external");
//        Server.getInstance(this).post(path,
//                getJsonDataView(), new Server.Callback() {
//                    @Override
//                    public void onError(JSONObject result) {
//                        printMessage(result);
//                        loading(false);
//                    }
//
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        loading(false);
//                        printMessage(result);
//                        finish();
//                    }
//                });
    }

    private void printMessage(final JSONObject response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.has("code") && !response.has("unauthorized")) {
                        if (response.getInt("code") == 409) { // duplicate
                            Toast.makeText(getApplicationContext(), R.string.validate_register_user_not_duplicate, Toast.LENGTH_LONG).show();
                        } else if (response.getInt("code") == 201) {
                            Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
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
    private void loading(final boolean enabled) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (menu != null) {
//                    final MenuItem menuItem = menu.findItem(R.id.action_save);
//                    menuItem.setEnabled(!enabled);
//                }
//                if (enabled) mProgressBar.setVisibility(View.VISIBLE);
//                else mProgressBar.setVisibility(View.GONE);
//            }
//        });
    }

    public boolean validate() {
//        String encodedId = encodedIdEditText.getText().toString();
//        String name = nameEditText.getText().toString();
//
//        if (encodedId.isEmpty()) {
//            encodedIdEditText.setError(getString(R.string.required_field));
//            requestFocus(encodedIdEditText);
//            return false;
//        } else {
//            encodedIdEditText.setError(null);
//        }
//
//        if (name.isEmpty() || name.length() < 3) {
//            nameEditText.setError(getString(R.string.validate_name));
//            requestFocus(nameEditText);
//            return false;
//        } else {
//            nameEditText.setError(null);
//        }

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

    public String getJsonDataView() {
        Elderly elderly = new Elderly();
        return new Gson().toJson(elderly);
    }

    @Override
    public void onNextPageSelected() {
        Log.d(TAG, "onNextPageSelected()");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        openFragment(ElderlyFormFragment.newInstance());
    }
}
