package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.fragment.ConnectDeviceFragment;
import br.edu.uepb.nutes.haniot.fragment.ScanDeviceFragment;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, application start.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivity";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBarToolbar)
    ProgressBar mProgressBar;

    private Fragment fragment;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Setup drawer view
        setupDrawerContent();

        fragment = new ConnectDeviceFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * User not logged
         */
        if (!(new Session(this).isLogged())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        openFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_synchronization:
                synchronizationWithServer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect_devices:
                fragment = new ConnectDeviceFragment();
                break;
            case R.id.action_scanner_devices:
                fragment = new ScanDeviceFragment();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }

        openFragment(fragment);

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupDrawerContent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void openFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment).commit();
//            transaction.replace(R.id.content, fragment).addToBackStack(null).commit();
        }
    }

    private void synchronizationWithServer() {
        changeSynchronization(true);

        SynchronizationServer.getInstance(this)
                .run(new SynchronizationServer.Callback() {
                    @Override
                    public void onError(JSONObject result) {
                        changeSynchronization(false);
                        showToast(getString(R.string.synchronization_failed));
                    }

                    @Override
                    public void onSuccess(JSONObject result) {
                        changeSynchronization(false);
                        String message = getString(R.string.synchronization_success);

                        if (result.has("message")) {
                            try {
                                message = result.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        showToast(message);
                    }
                });
    }

    private void changeSynchronization(final boolean isSynchronizing) {
        if (mMenu != null) {
            final MenuItem menuSynchronizing = mMenu.findItem(R.id.action_synchronization);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    menuSynchronizing.setVisible(!isSynchronizing);

                    if (isSynchronizing) mProgressBar.setVisibility(View.VISIBLE);
                    else mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showToast(final String menssage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                toast.show();
            }
        });
    }

}
