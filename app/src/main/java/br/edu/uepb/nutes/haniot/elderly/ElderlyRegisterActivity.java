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

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ActionBar actionBar;
    private String elderlyId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_register);
        ButterKnife.bind(this);

        Intent it = getIntent();
        elderlyId = it.getStringExtra(FallRiskActivity.EXTRA_ELDERLY_ID);

        initComponents();
    }

    private void initComponents() {
        initToolBar();
        if (elderlyId != null) {
            Fragment fragment = ElderlyFormFragment.newInstance();
            Bundle args = new Bundle();
            args.putString(ElderlyFormFragment.EXTRA_ELDERLY_ID, elderlyId);
            fragment.setArguments(args);
            openFragment(fragment);
        } else {
            openFragment(ElderlyPinFragment.newInstance());
        }
    }

    /**
     * Open fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment instanceof ElderlyFormFragment) {
            actionBar.setTitle(getString(R.string.elderly_registration));

            // Come from the ElderlyPinFragment
            if (fragment.getArguments().getString(ElderlyFormFragment.EXTRA_ELDERLY_ID) == null)
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            actionBar.setTitle(getString(R.string.elderly_associate_device));
        }

        transaction.replace(R.id.content_form_elderly, fragment).commit();
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
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
    public void onNextPageSelected(String pin) {
        Fragment fragment = ElderlyFormFragment.newInstance();
        Bundle args = new Bundle();
        args.putString(ElderlyFormFragment.EXTRA_ELDERLY_PIN, pin);
        fragment.setArguments(args);
        openFragment(fragment);
    }

    @Override
    public void onFormResult(Elderly elderly) {
        if (elderly == null) finish();

        runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.elderly_register_success));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                Intent it = new Intent(this, FallRiskActivity.class);
                it.putExtra(FallRiskActivity.EXTRA_ELDERLY_ID, elderly.get_id());
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
