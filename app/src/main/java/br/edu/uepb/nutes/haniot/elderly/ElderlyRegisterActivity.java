package br.edu.uepb.nutes.haniot.elderly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Elderly;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ElderlyRegisterActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyRegisterActivity extends AppCompatActivity implements
        ElderlyPinFragment.OnNextPageSelectedListener, ElderlyFormFragment.OnFormListener {
    private final String TAG = "ElderlyRegisterActivity";
    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

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
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.elderly_add));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNextPageSelected() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close_light);
        openFragment(ElderlyFormFragment.newInstance());
    }

    @Override
    public void onFormResult(Elderly elderly) {
        Log.d(TAG, "onFormResult() ".concat(elderly.toString()));
        runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.elderly_register_success));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                Intent it = new Intent(this, FallRiskActivity.class);
                it.putExtra(EXTRA_ELDERLY_ID, elderly.get_id());
                startActivity(it);
            });

            dialog.setNegativeButton(R.string.no_text, (dialogInterface, which) -> {
                finish();
            });

            dialog.setOnCancelListener((dialogInterface) -> {
                finish();
            });

            dialog.create().show();
        });
    }

}
